package chat;

import java.io.ObjectInputStream;

public class ChatClientListener extends Thread {
	private ObjectInputStream in;
	private ChatClientSocket clientSocket;
	
	public ChatClientListener(ObjectInputStream in, ChatClientSocket clientSocket) {
		this.in = in;
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			Object obj;
			while ((obj = in.readObject()) != null) {
				if (obj instanceof String msg) {
					if (clientSocket.getAdminPanel() != null) {
						clientSocket.getAdminPanel().receiveMessage(msg);
					} else if (clientSocket.getUserPanel() != null) {
						clientSocket.getUserPanel().receiveMessage(msg);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[CLIENT LISTENER] Mất kết nối tới server: " + e.getMessage());
		}
	}
}
