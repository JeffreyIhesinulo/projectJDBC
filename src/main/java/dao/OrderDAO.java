package dao;
import java.sql.*;
import model.Order;
import model.OrderItem;
import util.DB;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import model.CartItem;

public class OrderDAO {
	
	public int createOrder(Connection con) throws SQLException
	{
		String sql = "INSERT INTO orders(total_price, created_at) VALUES (?, ?)";
		
		try(
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
		{
			ps.setDouble(1,  0.0);
			ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			
			int affected = ps.executeUpdate();
			if (affected == 0) 
				return -1;//error
			
			try(ResultSet keys = ps.getGeneratedKeys())
			{
				if(keys.next())
					return keys.getInt(1);
			}
			
		}
			return -1;
				
			
	}
	
	public boolean addItem(Connection con, int orderId, int menuId, int qty) throws SQLException
	{
		String sql = "INSERT INTO order_items(order_id, menu_id, qty) VALUES(?,?,?)";
		try(PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, orderId);
			ps.setInt(2, menuId);
			ps.setInt(3, qty);
			
			return ps.executeUpdate() == 1;
			
			
		}
	}
	
	public boolean updateTotal(Connection con, int orderId) throws SQLException
	{
		String sql  =
				"UPDATE orders " +
				        "SET total_price = (" +
				        "  SELECT COALESCE(SUM(oi.qty * m.price), 0) " +
				        "  FROM order_items oi " +
				        "  JOIN menu m ON m.menu_id = oi.menu_id " +
				        "  WHERE oi.order_id = ?" +
				        ") " +
				        "WHERE order_id = ?";
		try(
			PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, orderId);
			ps.setInt(2, orderId);
			
			return ps.executeUpdate() == 1;
			
		}
	}
	
	public Order findOrderById(int orderId) throws Exception
	{
		String sql = "SELECT id, total_price, created_at FROM orders WHERE order_id = ? ";
		
		try(Connection con = DB.getConnection();
			PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, orderId);
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					return new Order(
					rs.getInt("id"),
					rs.getDouble("total_price"),
					rs.getTimestamp("created_at").toLocalDateTime()
							);
				}
			}
		}
		return null;
	}
	
	public List<OrderItem> findItemByOrderId(int orderId) throws Exception
	{
		String sql = "SELECT id, order_id, menu_id, qty FROM order_items WHERE order_id = ? ORDER BY id";
		List<OrderItem> items = new ArrayList<>();
		try(Connection con = DB.getConnection();
			PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, orderId);
			
			try(ResultSet rs = ps.executeQuery())
			{
				while(rs.next())
				{
					items.add(new OrderItem(
						rs.getInt("id"),
						rs.getInt("order_id"),
						rs.getInt("menu_id"),
						rs.getInt("qty")
							));
				}
			}
		}
		return items;
	}
	
	public int placeOrder(List<CartItem> cart) throws SQLException
	{
		if (cart == null || cart.isEmpty()) return -1;
		
		Connection con = null;
		try
		{
			con = DB.getConnection();
			con.setAutoCommit(false);
			
			int orderId = createOrder(con);
			if(orderId == -1) throw new SQLException("Failed to create order");
			
			for (CartItem item : cart)
			{
				if(item.getQty() <= 0)
				{
					throw new SQLException("Invalid qty: "+ item.getQty());
				}
				boolean ok = addItem(con, orderId, item.getMenuId(), item.getQty());
				if(!ok) throw new SQLException("Failed to add order item");
			}
			
			boolean totalOk = updateTotal(con, orderId);
			if(!totalOk) throw new SQLException("Failed to update total");
			
			con.commit();
			return orderId;
		}
		catch(SQLException e)
		{
			if(con != null)
			{
				try
				{
					con.rollback();
				}
				catch(SQLException ignored)
				{
					
				}
			}
			throw e;
		}
		finally {
					if(con != null)
					{
						try
						{
							con.setAutoCommit(true);
						}
						catch(SQLException ignored)
						{
							
						}
						try
						{
							con.close();
						}
						catch(SQLException ignored)
						{
							
						}
					}
				}
	}

}
