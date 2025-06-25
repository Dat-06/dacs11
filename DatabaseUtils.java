package main;


import internetbilling.DatabaseConnection;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility class for database operations
 */
public class DatabaseUtils {
	private static final Logger LOGGER = Logger.getLogger(DatabaseUtils.class.getName());
	
	/**
	 * Initialize database with sample data if empty
	 */
	public static void initializeSampleData() {
		try (Connection conn = DatabaseConnection.getConnection()) {
			// Check if admin user exists
			String checkAdminQuery = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
			try (PreparedStatement stmt = conn.prepareStatement(checkAdminQuery);
			     ResultSet rs = stmt.executeQuery()) {
				
				if (rs.next() && rs.getInt(1) == 0) {
					// Insert default admin user
					String insertAdmin = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
					try (PreparedStatement adminStmt = conn.prepareStatement(insertAdmin)) {
						adminStmt.setString(1, "admin");
						adminStmt.setString(2, "admin123");
						adminStmt.setString(3, "Quản trị viên hệ thống");
						adminStmt.setString(4, "admin@internetbilling.com");
						adminStmt.setString(5, "admin");
						adminStmt.executeUpdate();
						LOGGER.info("Default admin user created successfully");
					}
				}
			}
			
			// Check if packages exist
			String checkPackagesQuery = "SELECT COUNT(*) FROM packages";
			try (PreparedStatement stmt = conn.prepareStatement(checkPackagesQuery);
			     ResultSet rs = stmt.executeQuery()) {
				
				if (rs.next() && rs.getInt(1) == 0) {
					insertSamplePackages(conn);
					LOGGER.info("Sample packages created successfully");
				}
			}
			
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error initializing sample data", e);
		}
	}
	
	private static void insertSamplePackages(Connection conn) throws SQLException {
		String insertPackage = "INSERT INTO packages (name, speed, price, description, is_active) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(insertPackage)) {
			// Package 1: Home Basic
			stmt.setString(1, "Gói Home Basic");
			stmt.setString(2, "50 Mbps");
			stmt.setBigDecimal(3, new java.math.BigDecimal("300000"));
			stmt.setString(4, "Gói internet cơ bản cho gia đình, tốc độ 50 Mbps");
			stmt.setBoolean(5, true);
			stmt.addBatch();
			
			// Package 2: Home Premium
			stmt.setString(1, "Gói Home Premium");
			stmt.setString(2, "100 Mbps");
			stmt.setBigDecimal(3, new java.math.BigDecimal("500000"));
			stmt.setString(4, "Gói internet cao cấp cho gia đình, tốc độ 100 Mbps");
			stmt.setBoolean(5, true);
			stmt.addBatch();
			
			// Package 3: Business Standard
			stmt.setString(1, "Gói Business Standard");
			stmt.setString(2, "200 Mbps");
			stmt.setBigDecimal(3, new java.math.BigDecimal("800000"));
			stmt.setString(4, "Gói internet doanh nghiệp, tốc độ 200 Mbps");
			stmt.setBoolean(5, true);
			stmt.addBatch();
			
			// Package 4: Business Pro
			stmt.setString(1, "Gói Business Pro");
			stmt.setString(2, "500 Mbps");
			stmt.setBigDecimal(3, new java.math.BigDecimal("1500000"));
			stmt.setString(4, "Gói internet doanh nghiệp cao cấp, tốc độ 500 Mbps");
			stmt.setBoolean(5, true);
			stmt.addBatch();
			
			// Package 5: Ultra Speed
			stmt.setString(1, "Gói Ultra Speed");
			stmt.setString(2, "1 Gbps");
			stmt.setBigDecimal(3, new java.math.BigDecimal("2500000"));
			stmt.setString(4, "Gói internet siêu tốc, tốc độ 1 Gbps");
			stmt.setBoolean(5, true);
			stmt.addBatch();
			
			stmt.executeBatch();
		}
	}
	
	/**
	 * Test database connection
	 */
	public static boolean testConnection() {
		try (Connection conn = DatabaseConnection.getConnection()) {
			return conn != null && !conn.isClosed();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Database connection test failed", e);
			return false;
		}
	}
	
	/**
	 * Get database statistics
	 */
	public static String getDatabaseStats() {
		StringBuilder stats = new StringBuilder();
		try (Connection conn = DatabaseConnection.getConnection()) {
			String[] tables = {"users", "packages", "subscriptions", "transactions", "messages"};
			
			for (String table : tables) {
				String query = "SELECT COUNT(*) FROM " + table;
				try (PreparedStatement stmt = conn.prepareStatement(query);
				     ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						stats.append(table).append(": ").append(rs.getInt(1)).append(" records\n");
					}
				}
			}
		} catch (SQLException e) {
			stats.append("Error getting database statistics: ").append(e.getMessage());
		}
		return stats.toString();
	}
	
	/**
	 * Clean up old data (optional maintenance function)
	 */
	public static void cleanupOldData() {
		try (Connection conn = DatabaseConnection.getConnection()) {
			// Delete messages older than 30 days
			String cleanupMessages = "DELETE FROM messages WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";
			try (PreparedStatement stmt = conn.prepareStatement(cleanupMessages)) {
				int deleted = stmt.executeUpdate();
				LOGGER.info("Cleaned up " + deleted + " old messages");
			}
			
			// Delete failed transactions older than 7 days
			String cleanupTransactions = "DELETE FROM transactions WHERE status = 'failed' AND transaction_date < DATE_SUB(NOW(), INTERVAL 7 DAY)";
			try (PreparedStatement stmt = conn.prepareStatement(cleanupTransactions)) {
				int deleted = stmt.executeUpdate();
				LOGGER.info("Cleaned up " + deleted + " failed transactions");
			}
			
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Error during cleanup", e);
		}
	}
}