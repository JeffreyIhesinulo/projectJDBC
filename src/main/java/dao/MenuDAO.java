package dao;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import model.Menu;
/*
* 1. import ------> java.sql
* 2. Create a connection ----> connection
* 3. Create a statement -----> statement
* 4. execute the query ---->
* 5. process the results ->
* 6. close
*/
import util.DB;

public class MenuDAO {
	
	public List<Menu> findAll() throws SQLException
	{
		String sql = "SELECT menu_id, dish, price FROM menu ORDER BY menu_id";//message to db
		List<Menu> list = new ArrayList<>();//list of dishes 'Menu'
		
		try (Connection con = DB.getConnection();//create connection - leads to util, where the class connection class resides
				PreparedStatement ps = con.prepareStatement(sql);//creating prepared statement injecting with our message to db
				ResultSet rs = ps.executeQuery())//executing the query
				
		{
			while(rs.next())
			{
				Menu dish = new Menu(
					rs.getInt("menu_id"),
					rs.getString("dish"),
					rs.getDouble("price")
					); //filling the Menu constructor
				list.add(dish);
			}
			
			
		}
		return list;	
	}
	
	public Menu findById(int id) throws SQLException
	{
		String sql = "SELECT menu_id, dish, price FROM menu WHERE menu_id = ?";
		
		try(Connection con = DB.getConnection();
			PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, id);
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					return new Menu(
							rs.getInt("menu_id"),
							rs.getString("dish"),
							rs.getDouble("price")
							);
				}
			}
		}
		return null;
				
				
		
	}
	
	public int insert(String dish, double price) throws SQLException
	{
		String sql = "INSERT INTO menu(dish, price) VALUES (?, ?)";//db statement
		
		try(Connection con = DB.getConnection();//connection do db
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)//preparedStatement with generated keys from db e.g. id 17 
					)
			{
				ps.setString(1, dish);//setting (?,?) to (dish, price)
				ps.setDouble(2, price);//
				
				int affected = ps.executeUpdate();//created affected value and set it to what db provides
				if(affected == 0)
				{
					return -1;//return error
				}
				try(ResultSet keys = ps.getGeneratedKeys())//
				{
					if(keys.next())//if found
						return keys.getInt(1);//return id
				}
			}
		return -1;//return error
		
		
		
	}
	
	public boolean update(int id, String dish, double price) throws SQLException
	{
		String sql = "UPDATE menu Set dish = ?, price = ?, WHERE menu_id = ?";
		
		try(Connection con = DB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setString(1, dish);
			ps.setDouble(2, price);
			ps.setInt(3, id);
			
			return ps.executeUpdate() == 1;
			
		}
		
	}
	
	public boolean delete(int id) throws SQLException
	{
		String sql = "DELETE FROM menu WHERE menu_id = ?";
		try(Connection con = DB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setInt(1, id);
			return ps.executeUpdate() == 1;
		}
		
	}

	}
	