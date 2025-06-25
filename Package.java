package goicuoc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Model class cho Package (Gói Internet)
 */
public class Package {
	private int id;
	private String name;
	private String speed;
	private BigDecimal price;
	private String description;
	private boolean isActive;
	private boolean autoRenew;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	
	public Package() {}
	
	public Package(int id, String name, String speed, BigDecimal price,
	               String description, boolean isActive, boolean autoRenew, Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.name = name;
		this.speed = speed;
		this.price = price;
		this.description = description;
		this.isActive = isActive;
		this.autoRenew = autoRenew;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Package(String name, String speed, BigDecimal price, String description, boolean isActive, boolean autoRenew) {
		this.name = name;
		this.speed = speed;
		this.price = price;
		this.description = description;
		this.isActive = isActive;
		this.autoRenew = autoRenew;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSpeed() {
		return speed;
	}
	
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean active) {
		isActive = active;
	}
	
	public boolean isAutoRenew() {
		return autoRenew;
	}
	
	public void setAutoRenew(boolean autoRenew) {
		this.autoRenew = autoRenew;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s (%s VND/tháng)%s", name, speed, price, autoRenew ? " [Auto Renew]" : "");
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Package aPackage = (Package) o;
		return id == aPackage.id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
