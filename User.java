package users;

import java.time.LocalDateTime;

public class User {
	private int id;
	private String username;
	private String password;
	private String fullName;
	private String email;
	private String phone;
	private String address;
	private String role;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String avatarPath;
	
	public String getAvatarPath() {
		return avatarPath;
	}
	
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	
	// Constructor mặc định
	public User() {}
	
	// Constructor 4 tham số cơ bản
	public User(String username, String password, String fullName, String email) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
	}
	
	// Constructor 7 tham số dùng cho đăng ký
	public User(String username, String password, String fullName, String email, String phone, String address, String role) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.role = role;
	}
	
	// Constructor đầy đủ 10 tham số
	public User(int id, String username, String password, String fullName, String email,
	            String phone, String address, String role,
	            LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
	public String toString() {
		return "User{id=" + id +
				", username='" + username + '\'' +
				", fullName='" + fullName + '\'' +
				", email='" + email + '\'' +
				", role='" + role + '\'' +
				'}';
	}
}
