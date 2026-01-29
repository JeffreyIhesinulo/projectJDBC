package model;

public class OrderItem {
	private int id;
	private int order_id;
	private int menu_id;
	private int qty;
	public OrderItem(int id, int order_id, int menu_id, int qty)
	{

		this.id = id;
		this.order_id = order_id;
		this.menu_id = menu_id;
		this.qty = qty;
	}
	public int getId() {
		return id;
	}
	public int getOrderId() {
		return order_id;
	}
	
	public int getMenuId() {
		return menu_id;
	}
	
	public int getQty() {
		return qty;
	}
	
	


}
