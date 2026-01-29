package model;

import java.util.*;
import java.util.List;
import java.util.HashMap;
public class Cart {

   
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    public void add(int menuId, int qty) 
    {
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");//the quantity checker

        CartItem existing = items.get(menuId); //creating an instance of CartItem called existing
        if (existing == null) {
            items.put(menuId, new CartItem(menuId, qty));
        } else {
            existing.setQty(existing.getQty() + qty);
        }
    } 
    
    public void setQty(int menuId, int qty)
    {
    	 if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
    	 
    	 if(qty == 0)
    	 {
    		 items.remove(menuId);
    		 return;
    	 }
    	 CartItem existing = items.get(menuId);
    	 if(existing == null)
    	 {
    		 items.put(menuId, new CartItem(menuId, qty));
    	 }
    	 else
    	 {
    		 existing.setQty(qty);
    	 }
    }
    
    public void remove(int menuId)
    {
    	items.remove(menuId);
    }
    
    public boolean contains(int menuId)
    {
    	return items.containsKey(menuId);
    }
    
    public boolean isEmpty() 
    {
        return items.isEmpty();
    }

    public void clear() 
    {
        items.clear();
    }
    public List<CartItem> toList() 
    {
        return new ArrayList<>(items.values());
    }
    
    public int uniqueItemsCount() 
    {
        return items.size();
    }
    public int totalQty() {
        int sum = 0;
        for (CartItem item : items.values()) sum += item.getQty();
        return sum;
    }
    
    
    
        
}
    
    
    
    
    
    
 