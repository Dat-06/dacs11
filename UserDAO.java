package users;

import internetbilling.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
	private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
	
	public boolean addUser(User user) {
		String sql = "INSERT INTO users (username, password, full_name, email, phone, address, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFullName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getPhone());
			stmt.setString(6, user.getAddress());
			stmt.setString(7, user.getRole());
			stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi khi thêm người dùng", e);
			return false;
		}
	}
	public boolean updateUserInfo(User user) {
		String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, address = ?, updated_at = NOW() WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getFullName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getPhone());
			stmt.setString(4, user.getAddress());
			stmt.setInt(5, user.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Optional<User> login(String username, String password) {
		String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return Optional.of(mapResultSetToUser(rs));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Đăng nhập thất bại", e);
		}
		return Optional.empty();
	}
	
	public boolean isUsernameExists(String username) {
		String sql = "SELECT 1 FROM users WHERE username = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi kiểm tra username", e);
			return false;
		}
	}
	
	public boolean isEmailExists(String email) {
		String sql = "SELECT 1 FROM users WHERE email = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi kiểm tra email", e);
			return false;
		}
	}
	
	public boolean updateUser(User user) {
		String sql = "UPDATE users SET username=?, password=?, full_name=?, email=?, phone=?, address=?, role=?, updated_at=? WHERE id=?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFullName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getPhone());
			stmt.setString(6, user.getAddress());
			stmt.setString(7, user.getRole());
			stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setInt(9, user.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi cập nhật người dùng", e);
			return false;
		}
	}
	
	public boolean deleteUser(int id) {
		String sql = "DELETE FROM users WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi xóa người dùng", e);
			return false;
		}
	}
	
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users ORDER BY id DESC";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				users.add(mapResultSetToUser(rs));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách người dùng", e);
		}
		return users;
	}
	
	public Optional<User> getUserById(int id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return Optional.of(mapResultSetToUser(rs));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi lấy user theo ID", e);
		}
		return Optional.empty();
	}
	
	public Optional<User> getUserByUsername(String username) {
		String sql = "SELECT * FROM users WHERE username = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return Optional.of(mapResultSetToUser(rs));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi lấy user theo username", e);
		}
		return Optional.empty();
	}
	
	public User findByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return mapResultSetToUser(rs);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi tìm theo email", e);
		}
		return null;
	}
	
	public boolean updatePassword(String email, String newPassword) {
		String sql = "UPDATE users SET password = ?, updated_at = ? WHERE email = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, newPassword);
			stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setString(3, email);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi cập nhật mật khẩu", e);
			return false;
		}
	}
	
	public boolean updateAvatar(int userId, String avatarPath) {
		String sql = "UPDATE users SET avatar_path = ?, updated_at = ? WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, avatarPath);
			stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setInt(3, userId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi cập nhật avatar", e);
			return false;
		}
	}
	
	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setFullName(rs.getString("full_name"));
		user.setEmail(rs.getString("email"));
		user.setPhone(rs.getString("phone"));
		user.setAddress(rs.getString("address"));
		user.setRole(rs.getString("role"));
		user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
		return user;
	}
}
