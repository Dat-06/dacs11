package goicuoc;

import users.User;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Model class cho Subscription (Đăng ký gói cước)
 */
public class Subscription {
	private int id;
	private int userId;
	private int packageId;
	private String status;
	private Date startDate;
	private Date endDate;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	// Liên kết đối tượng
	private User user;
	private Package packageInfo;
	
	// Constructors
	public Subscription() {}
	
	public Subscription(int id, int userId, int packageId, String status,
	                    Date startDate, Date endDate, Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.userId = userId;
		this.packageId = packageId;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Subscription(int userId, int packageId, String status, Date startDate, Date endDate) {
		this.userId = userId;
		this.packageId = packageId;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	// Getters & Setters
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public int getUserId() { return userId; }
	public void setUserId(int userId) { this.userId = userId; }
	
	public int getPackageId() { return packageId; }
	public void setPackageId(int packageId) { this.packageId = packageId; }
	
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	
	public Date getStartDate() { return startDate; }
	public void setStartDate(Date startDate) { this.startDate = startDate; }
	
	public Date getEndDate() { return endDate; }
	public void setEndDate(Date endDate) { this.endDate = endDate; }
	
	public Timestamp getCreatedAt() { return createdAt; }
	public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
	
	public Timestamp getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
	
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	
	public Package getPackageInfo() { return packageInfo; }
	public void setPackageInfo(Package packageInfo) { this.packageInfo = packageInfo; }
	
	// Kiểm tra có cần gia hạn hay không
	public boolean isEligibleForAutoRenew() {
		return "active".equalsIgnoreCase(status) &&
				endDate != null &&
				packageInfo != null &&
				packageInfo.isAutoRenew() &&
				endDate.before(new Date(System.currentTimeMillis()));
	}
	
	@Override
	public String toString() {
		return String.format("Subscription #%d [%s] | UserID: %d | PackageID: %d",
				id, status, userId, packageId);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Subscription)) return false;
		Subscription that = (Subscription) o;
		return id == that.id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
