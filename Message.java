//package chat;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//public class Message implements Serializable {
//	private int senderId;
//	private int receiverId; // -1 nghĩa là broadcast (gửi tới tất cả)
//	private String content;
//	private LocalDateTime timestamp;
//
//	public Message(int senderId, int receiverId, String content) {
//		this.senderId = senderId;
//		this.receiverId = receiverId;
//		this.content = content;
//		this.timestamp = LocalDateTime.now();
//	}
//
//	public int getSenderId() { return senderId; }
//	public int getReceiverId() { return receiverId; }
//	public String getContent() { return content; }
//	public LocalDateTime getTimestamp() { return timestamp; }
//
//	public void setContent(String content) { this.content = content; }
//
//
//}
