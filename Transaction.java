package lichsu;

import goicuoc.Subscription;
import users.User;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class cho Transaction (Giao dịch)
 */
public class Transaction {
	private int id;
	private int userId;
	private Integer subscriptionId;
	private BigDecimal amount;
	private String status;
	private String type;
	private String description;
	private Timestamp transactionDate;
	
	private User user;
	private Subscription subscription;
	
	public Transaction() {}
	
	// Constructor without ID (for inserts)
	public Transaction(int userId, Integer subscriptionId, BigDecimal amount, String status, String type, String description) {
		this.userId = userId;
		this.subscriptionId = subscriptionId;
		this.amount = amount;
		this.status = status;
		this.type = type;
		this.description = description;
	}
	
	// Full constructor
	public Transaction(int id, int userId, Integer subscriptionId, BigDecimal amount,
	                   String status, String type, String description, Timestamp transactionDate) {
		this.id = id;
		this.userId = userId;
		this.subscriptionId = subscriptionId;
		this.amount = amount;
		this.status = status;
		this.type = type;
		this.description = description;
		this.transactionDate = transactionDate;
	}
	
	// Getters and setters...
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public int getUserId() { return userId; }
	public void setUserId(int userId) { this.userId = userId; }
	
	public Integer getSubscriptionId() { return subscriptionId; }
	public void setSubscriptionId(Integer subscriptionId) { this.subscriptionId = subscriptionId; }
	
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public Timestamp getTransactionDate() { return transactionDate; }
	public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }
	
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	
	public Subscription getSubscription() { return subscription; }
	public void setSubscription(Subscription subscription) { this.subscription = subscription; }
}
