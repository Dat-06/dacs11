package goicuoc;

import internetbilling.DatabaseConnection;
import users.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Subscription
 */
public class SubscriptionDAO {
	
	private static final String BASE_QUERY = """
		SELECT s.*,
		       u.username, u.full_name, u.email, u.role,
		       p.name AS package_name, p.speed, p.price, p.description,
		       s.auto_renew
		FROM subscriptions s
		JOIN users u ON s.user_id = u.id
		JOIN packages p ON s.package_id = p.id
	""";
	
	public List<Subscription> getAllSubscriptions() {
		String sql = BASE_QUERY + " ORDER BY s.created_at DESC";
		return getSubscriptionsByQuery(sql);
	}
	
	public List<Subscription> getSubscriptionsByUserId(int userId) {
		String sql = BASE_QUERY + " WHERE s.user_id = ? ORDER BY s.created_at DESC";
		return getSubscriptionsByParam(sql, userId);
	}
	
	public Subscription getSubscriptionById(int id) {
		String sql = BASE_QUERY + " WHERE s.id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToSubscription(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy subscription theo ID: " + e.getMessage());
		}
		return null;
	}
	
	public boolean addSubscription(Subscription s) {
		String sql = "INSERT INTO subscriptions (user_id, package_id, status, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, s.getUserId());
			stmt.setInt(2, s.getPackageId());
			stmt.setString(3, s.getStatus());
			stmt.setDate(4, s.getStartDate());
			stmt.setDate(5, s.getEndDate());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Lỗi thêm subscription: " + e.getMessage());
			return false;
		}
	}
	
	public boolean updateSubscription(Subscription s) {
		String sql = "UPDATE subscriptions SET user_id = ?, package_id = ?, status = ?, start_date = ?, end_date = ?, updated_at = ? WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, s.getUserId());
			stmt.setInt(2, s.getPackageId());
			stmt.setString(3, s.getStatus());
			stmt.setDate(4, s.getStartDate());
			stmt.setDate(5, s.getEndDate());
			stmt.setTimestamp(6, s.getUpdatedAt());
			stmt.setInt(7, s.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Lỗi cập nhật subscription: " + e.getMessage());
			return false;
		}
	}
	
	public boolean deleteSubscription(int id) {
		String sql = "DELETE FROM subscriptions WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Lỗi xóa subscription: " + e.getMessage());
			return false;
		}
	}
	
	public List<Subscription> getActiveSubscriptions() {
		String sql = BASE_QUERY + " WHERE s.status = 'active' ORDER BY s.created_at DESC";
		return getSubscriptionsByQuery(sql);
	}
	
	public List<Subscription> searchSubscriptions(String keyword) {
		String sql = BASE_QUERY + """
			WHERE u.full_name LIKE ? OR u.username LIKE ? OR p.name LIKE ?
			ORDER BY s.created_at DESC
		""";
		List<Subscription> subscriptions = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			String pattern = "%" + keyword + "%";
			stmt.setString(1, pattern);
			stmt.setString(2, pattern);
			stmt.setString(3, pattern);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					subscriptions.add(mapResultSetToSubscription(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi tìm kiếm subscriptions: " + e.getMessage());
		}
		return subscriptions;
	}
	
	public int autoRenewSubscriptions() {
		int renewCount = 0;
		List<Subscription> activeSubs = getActiveSubscriptions();
		for (Subscription s : activeSubs) {
			if (s.getPackageInfo() != null && s.getPackageInfo().isAutoRenew()) {
				LocalDate today = LocalDate.now();
				LocalDate end = s.getEndDate().toLocalDate();
				if (end.isBefore(today)) {
					LocalDate newStart = today;
					LocalDate newEnd = newStart.plus(30, ChronoUnit.DAYS);
					s.setStartDate(Date.valueOf(newStart));
					s.setEndDate(Date.valueOf(newEnd));
					s.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					if (updateSubscription(s)) {
						renewCount++;
						System.out.println("✔ Gia hạn subscription #" + s.getId() + " cho user: " + s.getUser().getUsername());
					}
				}
			}
		}
		return renewCount;
	}
	
	private List<Subscription> getSubscriptionsByQuery(String sql) {
		List<Subscription> subscriptions = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				subscriptions.add(mapResultSetToSubscription(rs));
			}
		} catch (SQLException e) {
			System.err.println("Lỗi truy vấn subscriptions: " + e.getMessage());
		}
		return subscriptions;
	}
	
	private List<Subscription> getSubscriptionsByParam(String sql, int param) {
		List<Subscription> subscriptions = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, param);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					subscriptions.add(mapResultSetToSubscription(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("Lỗi truy vấn theo tham số: " + e.getMessage());
		}
		return subscriptions;
	}
	
	private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
		Subscription s = new Subscription();
		s.setId(rs.getInt("id"));
		s.setUserId(rs.getInt("user_id"));
		s.setPackageId(rs.getInt("package_id"));
		s.setStatus(rs.getString("status"));
		s.setStartDate(rs.getDate("start_date"));
		s.setEndDate(rs.getDate("end_date"));
		s.setCreatedAt(rs.getTimestamp("created_at"));
		s.setUpdatedAt(rs.getTimestamp("updated_at"));
		
		User u = new User();
		u.setId(rs.getInt("user_id"));
		u.setUsername(rs.getString("username"));
		u.setFullName(rs.getString("full_name"));
		u.setEmail(rs.getString("email"));
		u.setRole(rs.getString("role"));
		s.setUser(u);
		
		Package p = new Package();
		p.setId(rs.getInt("package_id"));
		p.setName(rs.getString("package_name"));
		p.setSpeed(rs.getString("speed"));
		p.setPrice(rs.getBigDecimal("price"));
		p.setDescription(rs.getString("description"));
		p.setAutoRenew(rs.getBoolean("auto_renew"));
		s.setPackageInfo(p);
		
		return s;
	}
}
