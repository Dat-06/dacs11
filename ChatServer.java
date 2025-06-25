package chat;



import users.User;
import users.UserDAO;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
	private static final int PORT = 5000;
	private static final int ADMIN_ID = 1;
	private static final int BROADCAST_ID = 0;
	
	private static final Map<Integer, ObjectOutputStream> clientStreams = new ConcurrentHashMap<>();
	private static final ChatDAO chatDAO = new ChatDAO(); // 🔄
	private static final UserDAO userDAO = new UserDAO(); // 🔄
	
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("[SERVER] Đang chạy tại cổng " + PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(() -> handleClient(socket)).start();
			}
		} catch (IOException e) {
			System.err.println("[SERVER] Lỗi khởi tạo: " + e.getMessage());
		}
	}
	
	private static void handleClient(Socket socket) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		int userId = -1;
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			// Nhận userId:login
			String raw = (String) in.readObject();
			userId = Integer.parseInt(raw.split(":")[0]);
			
			clientStreams.put(userId, out);
			System.out.println("[SERVER] ✅ Kết nối user ID: " + userId);
			
			if (userId == ADMIN_ID) {
				sendUserListToAdmin(); // 🔄 lấy từ MySQL
			} else {
				sendUndeliveredMessages(userId); // 🔄 gửi tin chưa đọc từ DB
			}
			
			Object obj;
			while ((obj = in.readObject()) != null) {
				if (obj instanceof String msg) {
					if (msg.equals("USER_LIST_REQUEST") && userId == ADMIN_ID) {
						sendUserListToAdmin();
						continue;
					}
					
					String[] parts = msg.split(":", 3);
					if (parts.length < 3) continue;
					
					int sender = Integer.parseInt(parts[0]);
					int receiver = Integer.parseInt(parts[1]);
					String content = parts[2];
					
					// Cấm user gửi user
					if (sender != ADMIN_ID && receiver != ADMIN_ID) {
						sendSystemMessage(sender, "❌ Bạn chỉ có thể nhắn cho Admin.");
						continue;
					}
					
					// Lưu tin nhắn vào DB
					chatDAO.saveMessage(new ChatMessage(sender, receiver, content)); // 🔄
					
					// Nếu admin offline
					if (sender != ADMIN_ID && receiver == ADMIN_ID && !clientStreams.containsKey(ADMIN_ID)) {
						sendSystemMessage(sender, "🤖 Admin hiện đang offline. Vui lòng để lại lời nhắn.");
						continue;
					}
					
					// Broadcast
					if (sender == ADMIN_ID && receiver == BROADCAST_ID) {
						broadcastToUsers(sender, content);
					} else {
						forwardMessage(sender, receiver, content);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[SERVER] ❗ Lỗi với client (ID: " + userId + "): " + e.getMessage());
		} finally {
			if (userId != -1) {
				clientStreams.remove(userId);
				System.out.println("[SERVER] ❌ User ID " + userId + " đã ngắt kết nối.");
				if (userId == ADMIN_ID) {
					// admin off -> không gửi gì
				} else {
					sendUserListToAdmin();
				}
			}
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				socket.close();
			} catch (IOException ignored) {}
		}
	}
	
	private static void forwardMessage(int senderId, int receiverId, String content) {
		String fullMsg = senderId + ":" + content;
		try {
			ObjectOutputStream out = clientStreams.get(receiverId);
			if (out != null) {
				out.writeObject(fullMsg);
				out.flush();
			}
		} catch (IOException e) {
			System.err.println("[SERVER] Không gửi được tới user ID: " + receiverId);
		}
	}
	
	private static void broadcastToUsers(int senderId, String content) {
		String fullMsg = senderId + ":[Gửi tất cả] " + content;
		for (Map.Entry<Integer, ObjectOutputStream> entry : clientStreams.entrySet()) {
			int id = entry.getKey();
			if (id != ADMIN_ID) {
				try {
					entry.getValue().writeObject(fullMsg);
					entry.getValue().flush();
				} catch (IOException e) {
					System.err.println("[SERVER] Không gửi broadcast tới user " + id);
				}
			}
		}
	}
	
	private static void sendSystemMessage(int userId, String content) {
		try {
			ObjectOutputStream out = clientStreams.get(userId);
			if (out != null) {
				out.writeObject("BOT:" + content);
				out.flush();
			}
		} catch (IOException e) {
			System.err.println("[SERVER] Gửi BOT message thất bại cho " + userId);
		}
	}
	
	private static void sendUserListToAdmin() {
		List<User> allUsers = userDAO.getAllUsers(); // 🔄 từ DB
		StringBuilder userList = new StringBuilder("USER_LIST");
		for (User u : allUsers) {
			if (u.getId() != ADMIN_ID) {
				userList.append(":").append(u.getId())
						.append(",").append(u.getFullName())
						.append(",").append(clientStreams.containsKey(u.getId()) ? "online" : "offline");
			}
		}
		try {
			ObjectOutputStream adminOut = clientStreams.get(ADMIN_ID);
			if (adminOut != null) {
				adminOut.writeObject(userList.toString());
				adminOut.flush();
				System.out.println("[SERVER] 📤 Gửi danh sách user cho admin: " + userList);
			}
		} catch (IOException e) {
			System.err.println("[SERVER] Không gửi danh sách user tới admin.");
		}
	}
	
	private static void sendUndeliveredMessages(int userId) {
		List<ChatMessage> messages = chatDAO.getUnreadMessages(userId); // 🔄
		ObjectOutputStream out = clientStreams.get(userId);
		if (out != null) {
			for (ChatMessage msg : messages) {
				try {
					out.writeObject(msg.getSenderId() + ":" + msg.getContent());
					out.flush();
					chatDAO.markMessageAsDelivered(msg.getId()); // 🔄
				} catch (IOException ignored) {}
			}
		}
	}
}
