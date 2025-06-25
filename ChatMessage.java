package chat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {
	private int id; // 🔄 thêm ID để làm việc với DB
	private int senderId;
	private int receiverId;
	private String content;
	private boolean isFile;
	private byte[] fileData;
	private String fileName;
	private LocalDateTime timestamp;
	private boolean isRead;
	
	// Tin nhắn văn bản
	public ChatMessage(int senderId, int receiverId, String content) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.isFile = false;
		this.timestamp = LocalDateTime.now();
		this.isRead = false;
	}
	
	// Tin nhắn file
	public ChatMessage(int senderId, int receiverId, byte[] fileData, String fileName) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.fileData = fileData;
		this.fileName = fileName;
		this.isFile = true;
		this.timestamp = LocalDateTime.now();
		this.isRead = false;
	}
	
	// === Getters & Setters ===
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getSenderId() {
		return senderId;
	}
	
	public int getReceiverId() {
		return receiverId;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isFile() {
		return isFile;
	}
	
	public byte[] getFileData() {
		return fileData;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public boolean isRead() {
		return isRead;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setRead(boolean read) {
		isRead = read;
	}
}
