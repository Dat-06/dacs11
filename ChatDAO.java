package chat;

import internetbilling.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
	
	public void saveMessage(ChatMessage message) {
		String sql = "INSERT INTO chat_messages (sender_id, receiver_id, content, is_file, file_data, file_name, sent_at, is_read) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, false)";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, message.getSenderId());
			stmt.setInt(2, message.getReceiverId());
			stmt.setString(3, message.getContent());
			stmt.setBoolean(4, message.isFile());
			stmt.setBytes(5, message.getFileData());
			stmt.setString(6, message.getFileName());
			stmt.setTimestamp(7, Timestamp.valueOf(message.getTimestamp()));
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<ChatMessage> getChatHistory(int user1Id, int user2Id) {
		List<ChatMessage> messages = new ArrayList<>();
		String sql = "SELECT * FROM chat_messages " +
				"WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
				"ORDER BY sent_at ASC";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, user1Id);
			stmt.setInt(2, user2Id);
			stmt.setInt(3, user2Id);
			stmt.setInt(4, user1Id);
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ChatMessage msg;
					boolean isFile = rs.getBoolean("is_file");
					
					if (isFile) {
						msg = new ChatMessage(
								rs.getInt("sender_id"),
								rs.getInt("receiver_id"),
								rs.getBytes("file_data"),
								rs.getString("file_name")
						);
					} else {
						msg = new ChatMessage(
								rs.getInt("sender_id"),
								rs.getInt("receiver_id"),
								rs.getString("content")
						);
					}
					msg.setId(rs.getInt("id")); // cần thiết để mark là delivered
					msg.setTimestamp(rs.getTimestamp("sent_at").toLocalDateTime());
					msg.setRead(rs.getBoolean("is_read"));
					messages.add(msg);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
	public void markMessagesAsRead(int senderId, int receiverId) {
		String sql = "UPDATE chat_messages SET is_read = true WHERE sender_id = ? AND receiver_id = ? AND is_read = false";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, senderId);
			stmt.setInt(2, receiverId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// ✅ Lấy tất cả tin nhắn chưa đọc của người dùng
	public List<ChatMessage> getUnreadMessages(int receiverId) {
		List<ChatMessage> unread = new ArrayList<>();
		String sql = "SELECT * FROM chat_messages WHERE receiver_id = ? AND is_read = false ORDER BY sent_at ASC";
		
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, receiverId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ChatMessage msg;
					boolean isFile = rs.getBoolean("is_file");
					
					if (isFile) {
						msg = new ChatMessage(
								rs.getInt("sender_id"),
								rs.getInt("receiver_id"),
								rs.getBytes("file_data"),
								rs.getString("file_name")
						);
					} else {
						msg = new ChatMessage(
								rs.getInt("sender_id"),
								rs.getInt("receiver_id"),
								rs.getString("content")
						);
					}
					msg.setId(rs.getInt("id"));
					msg.setTimestamp(rs.getTimestamp("sent_at").toLocalDateTime());
					msg.setRead(false);
					unread.add(msg);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unread;
	}
	
	// ✅ Đánh dấu một tin nhắn là đã gửi thành công (delivered)
	public void markMessageAsDelivered(int messageId) {
		String sql = "UPDATE chat_messages SET is_read = true WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1, messageId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
