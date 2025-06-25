package goicuoc;

import internetbilling.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Package (Gói cước Internet)
 */
public class PackageDAO {
	
	private static final String SELECT_ALL = "SELECT * FROM packages ORDER BY name";
	private static final String SELECT_ACTIVE = "SELECT * FROM packages WHERE is_active = true ORDER BY price";
	private static final String SELECT_BY_ID = "SELECT * FROM packages WHERE id = ?";
	private static final String INSERT_PACKAGE = "INSERT INTO packages (name, speed, price, description, is_active) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PACKAGE = "UPDATE packages SET name = ?, speed = ?, price = ?, description = ?, is_active = ? WHERE id = ?";
	private static final String DELETE_PACKAGE = "DELETE FROM packages WHERE id = ?";
	private static final String SEARCH_PACKAGES = "SELECT * FROM packages WHERE name LIKE ? OR description LIKE ? ORDER BY name";
	private static final String CHECK_ACTIVE_SUBSCRIPTIONS = "SELECT COUNT(*) FROM subscriptions WHERE package_id = ? AND status = 'active'";
	
	public List<Package> getAllPackages() {
		List<Package> result = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
			while (rs.next()) {
				result.add(map(rs));
			}
		} catch (SQLException e) {
			logError("getAllPackages", e);
		}
		return result;
	}
	
	public List<Package> getActivePackages() {
		List<Package> result = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(SELECT_ACTIVE)) {
			while (rs.next()) {
				result.add(map(rs));
			}
		} catch (SQLException e) {
			logError("getActivePackages", e);
		}
		return result;
	}
	
	public Package getPackageById(int id) {
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return map(rs);
			}
		} catch (SQLException e) {
			logError("getPackageById", e);
		}
		return null;
	}
	
	public boolean addPackage(Package pkg) {
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(INSERT_PACKAGE)) {
			setParams(stmt, pkg, false);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			logError("addPackage", e);
		}
		return false;
	}
	
	public boolean updatePackage(Package pkg) {
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(UPDATE_PACKAGE)) {
			setParams(stmt, pkg, true);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			logError("updatePackage", e);
		}
		return false;
	}
	
	public boolean deletePackage(int id) {
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(DELETE_PACKAGE)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			logError("deletePackage", e);
		}
		return false;
	}
	
	public List<Package> searchPackages(String keyword) {
		List<Package> result = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(SEARCH_PACKAGES)) {
			String pattern = "%" + keyword + "%";
			stmt.setString(1, pattern);
			stmt.setString(2, pattern);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					result.add(map(rs));
				}
			}
		} catch (SQLException e) {
			logError("searchPackages", e);
		}
		return result;
	}
	
	public boolean hasActiveSubscriptions(int packageId) {
		try (Connection conn = DatabaseConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(CHECK_ACTIVE_SUBSCRIPTIONS)) {
			stmt.setInt(1, packageId);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			logError("hasActiveSubscriptions", e);
		}
		return false;
	}
	
	private Package map(ResultSet rs) throws SQLException {
		Package pkg = new Package();
		pkg.setId(rs.getInt("id"));
		pkg.setName(rs.getString("name"));
		pkg.setSpeed(rs.getString("speed"));
		pkg.setPrice(rs.getBigDecimal("price"));
		pkg.setDescription(rs.getString("description"));
		pkg.setActive(rs.getBoolean("is_active"));
		pkg.setCreatedAt(rs.getTimestamp("created_at"));
		pkg.setUpdatedAt(rs.getTimestamp("updated_at"));
		return pkg;
	}
	
	private void setParams(PreparedStatement stmt, Package pkg, boolean isUpdate) throws SQLException {
		stmt.setString(1, pkg.getName());
		stmt.setString(2, pkg.getSpeed());
		stmt.setBigDecimal(3, pkg.getPrice());
		stmt.setString(4, pkg.getDescription());
		stmt.setBoolean(5, pkg.isActive());
		if (isUpdate) stmt.setInt(6, pkg.getId());
	}
	
	private void logError(String method, Exception e) {
		System.err.printf("Lỗi trong %s: %s%n", method, e.getMessage());
	}
}
