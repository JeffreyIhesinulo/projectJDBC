package model;

public class CartItem {
	private final int menuId;
	private int qty;
	
	public CartItem(int menuId, int qty)
	{
		if(qty <= 0)throw new IllegalArgumentException("qty must be > 0");
		this.menuId = menuId;
		this.qty = qty;
	}
	
	public int getMenuId() 
	{

        return menuId;
	}
	
	public int getQty()
	{

        return qty;
	}
	
	public void setQty(int qty)
	{
		if(qty <= 0)throw new IllegalArgumentException("qty must be > 0");
		this.qty = qty;
	}
}
