package lichsu;

import internetbilling.DatabaseConnection;
import goicuoc.Subscription;
import goicuoc.SubscriptionDAO;
import users.User;
import users.UserDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object cho Transaction
 */
public class TransactionDAO {
	private static final Logger LOGGER = Logger.getLogger(TransactionDAO.class.getName());
	
	private static final String BASE_SELECT = """
        SELECT t.id, t.user_id, t.subscription_id, t.amount, t.status, t.type, t.description, t.transaction_date,
               u.id as u_id, u.username, u.full_name, u.role,
               s.id as s_id, s.status as subscription_status, s.start_date, s.end_date,
               p.id as p_id, p.name as package_name, p.speed, p.price as package_price
        FROM transactions t
        JOIN users u ON t.user_id = u.id
        LEFT JOIN subscriptions s ON t.subscription_id = s.id
        LEFT JOIN packages p ON s.package_id = p.id
    """;
	
	private List<Transaction> executeTransactionQuery(String sql, Object... params) {
		List<Transaction> list = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			ResultSet rs = stmt.executeQuery();
			SubscriptionDAO subDao = new SubscriptionDAO();
			UserDAO userDao = new UserDAO();
			while (rs.next()) {
				Transaction t = new Transaction(
						rs.getInt("id"),
						rs.getInt("user_id"),
						rs.getObject("subscription_id") != null ? rs.getInt("subscription_id") : null,
						rs.getBigDecimal("amount"),
						rs.getString("status"),
						rs.getString("type"),
						rs.getString("description"),
						rs.getTimestamp("transaction_date")
				);
				// set User
				User u = new User();
				u.setId(rs.getInt("u_id"));
				u.setFullName(rs.getString("full_name"));
				u.setUsername(rs.getString("username"));
				u.setRole(rs.getString("role"));
				t.setUser(u);
				
				// set Subscription
				Integer subId = rs.getObject("s_id") != null ? rs.getInt("s_id") : null;
				if (subId != null) {
					Subscription s = subDao.getSubscriptionById(subId);
					t.setSubscription(s);
				}
				list.add(t);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi truy vấn transaction", e);
		}
		return list;
	}
	
	public List<Transaction> getAllTransactions() {
		String sql = BASE_SELECT + " ORDER BY t.transaction_date DESC";
		return executeTransactionQuery(sql);
	}
	
	public List<Transaction> getTransactionsByUserId(int userId) {
		String sql = BASE_SELECT + " WHERE t.user_id = ? ORDER BY t.transaction_date DESC";
		return executeTransactionQuery(sql, userId);
	}
	
	public Transaction getTransactionById(int id) {
		String sql = BASE_SELECT + " WHERE t.id = ?";
		List<Transaction> list = executeTransactionQuery(sql, id);
		return list.isEmpty() ? null : list.get(0);
	}
	
	public boolean addTransaction(Transaction transaction) {
		String sql = "INSERT INTO transactions (user_id, subscription_id, amount, status, type, description) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, transaction.getUserId());
			if (transaction.getSubscriptionId() != null) stmt.setInt(2, transaction.getSubscriptionId()); else stmt.setNull(2, Types.INTEGER);
			stmt.setBigDecimal(3, transaction.getAmount());
			stmt.setString(4, transaction.getStatus());
			stmt.setString(5, transaction.getType());
			stmt.setString(6, transaction.getDescription());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi thêm transaction", e);
			return false;
		}
	}
	
	public boolean updateTransaction(Transaction transaction) {
		String sql = "UPDATE transactions SET user_id = ?, subscription_id = ?, amount = ?, status = ?, type = ?, description = ? WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, transaction.getUserId());
			if (transaction.getSubscriptionId() != null) stmt.setInt(2, transaction.getSubscriptionId()); else stmt.setNull(2, Types.INTEGER);
			stmt.setBigDecimal(3, transaction.getAmount());
			stmt.setString(4, transaction.getStatus());
			stmt.setString(5, transaction.getType());
			stmt.setString(6, transaction.getDescription());
			stmt.setInt(7, transaction.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi cập nhật transaction", e);
			return false;
		}
	}
	
	public boolean deleteTransaction(int id) {
		String sql = "DELETE FROM transactions WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Lỗi xóa transaction", e);
			return false;
		}
	}
	
	public List<Transaction> searchTransactions(String keyword) {
		String sql = BASE_SELECT + " WHERE u.full_name LIKE ? OR t.description LIKE ? OR p.name LIKE ? ORDER BY t.transaction_date DESC";
		String pattern = "%" + keyword + "%";
		return executeTransactionQuery(sql, pattern, pattern, pattern);
	}
}
