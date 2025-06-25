package chat;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ChatClientSocket {
	private final ObjectOutputStream out;
	private AdminChatPanel adminPanel;
	private ChatPanel userPanel;
	
	public ChatClientSocket(ObjectOutputStream out) {
		this.out = out;
	}
	
	// Bắt đầu lắng nghe từ server
	public void startListening(ObjectInputStream in, int userId) {
		new Thread(() -> {
			try {
				Object obj;
				while ((obj = in.readObject()) != null) {
					if (obj instanceof String msg) {
						if (userId == 1 && adminPanel != null) {
							adminPanel.receiveMessage(msg);
						} else if (userPanel != null) {
							userPanel.receiveMessage(msg);
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("[CLIENT SOCKET] Đã ngắt kết nối từ server: " + e.getMessage());
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(null, "Mất kết nối tới server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				});
			}
		}).start();
	}
	
	// Gửi tin nhắn văn bản
	public void sendMessage(int senderId, int receiverId, String content) {
		try {
			String message = senderId + ":" + receiverId + ":" + content;
			out.writeObject(message);
			out.flush();
			
			// Ghi log cả 2 phía
			ChatLogger.log(senderId, "Bạn", content);
			if (receiverId != 0) {
				ChatLogger.log(receiverId, "Người gửi " + senderId, content);
			}
		} catch (IOException e) {
			System.out.println("[CLIENT SOCKET] Gửi tin nhắn thất bại: " + e.getMessage());
		}
	}
	
	// Gửi emoji (giống như tin nhắn văn bản)
	public void sendEmoji(int senderId, int receiverId, String emojiUnicode) {
		sendMessage(senderId, receiverId, emojiUnicode);
	}
	
	// Gửi file
	public void sendFile(int senderId, int receiverId, File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = fis.readAllBytes();
			String fileNameMsg = "[File] " + file.getName();
			
			// Gửi tên file
			out.writeObject(senderId + ":" + receiverId + ":" + fileNameMsg);
			out.flush();
			
			// Gửi dữ liệu file
			out.writeObject(buffer);
			out.flush();
			
			ChatLogger.log(senderId, "Bạn", fileNameMsg);
		} catch (IOException e) {
			System.out.println("[CLIENT SOCKET] Gửi file thất bại: " + e.getMessage());
		}
	}
	
	// Yêu cầu danh sách user từ server (dành cho admin)
	public void requestUserList() {
		try {
			out.writeObject("USER_LIST_REQUEST");
			out.flush();
		} catch (IOException e) {
			System.out.println("[CLIENT SOCKET] Yêu cầu danh sách user thất bại: " + e.getMessage());
		}
	}
	
	// Setters
	public void setAdminPanel(AdminChatPanel adminPanel) {
		this.adminPanel = adminPanel;
	}
	
	public void setUserPanel(ChatPanel userPanel) {
		this.userPanel = userPanel;
	}
	
	// Getters
	public AdminChatPanel getAdminPanel() {
		return adminPanel;
	}
	
	public ChatPanel getUserPanel() {
		return userPanel;
	}
}
