package dao;
import java.sql.*;

import util.DB;
import model.Admin;


public class AdminDAO {
	
	public Admin login(String username, String password) throws SQLException{
		String sql = "SELECT id, username, password FROM admin WHERE username = ? AND password = ?";
		
		try(Connection con = DB.getConnection();
			PreparedStatement ps = con.prepareStatement(sql))
		{
			ps.setString(1, username);
			ps.setString(2, password);
			try(ResultSet rs = ps.executeQuery())
			{
				if(rs.next())
				{
					return new Admin
							(rs.getInt("id"),
							rs.getString("username"),
							rs.getString("password")
									);
				}
			}
		}
		return null;
		
	}
}
