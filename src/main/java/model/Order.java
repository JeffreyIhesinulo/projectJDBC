package model;
import java.time.LocalDateTime;

public class Order {
	private int orderId;
	private double totalPrice;
	private LocalDateTime createdAt;
	public Order(int id, double totalPrice, LocalDateTime createdAt) 
	{
		this.orderId = id;
		this.totalPrice = totalPrice;
		this.createdAt = createdAt;
	}
	public int getId() {
		return orderId;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	

}
