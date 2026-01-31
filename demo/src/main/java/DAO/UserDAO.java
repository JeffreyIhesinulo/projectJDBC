package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DB;
import Model.User;

public class UserDAO {



    public List<User> getAllUsers() throws SQLException
    {
        String sql = "SELECT * FROM user";
        List<User> users = new ArrayList<>();
        try(Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        )
        {
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                User user = new User(
                rs.getInt("user_id"),
                        rs.getString("user_name"),
                rs.getString("user_email"));
                users.add(user);
            }

        }
        return users;
    }
    public int addUser(User user) throws SQLException
    {
        String sql = "INSERT INTO user (user_name, user_email) VALUES (?, ?)";
        try(Connection con = DB.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        )
            {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getUserEmail());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys())
                {
                    if(rs.next())
                    {
                        return rs.getInt(1);
                    }
                }

            }
        return 0;
    }

    public int updateUser(User user) throws SQLException
    {
        try(Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE user SET user_name = ?, user_email = ? WHERE user_id = ?");)
        {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getUserEmail());
            ps.setInt(3, user.getUserId());
            ps.executeUpdate();
        }
        return 0;
    }
    public int deleteUser(int user_id) throws SQLException
    {
        try(Connection con = DB.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM user WHERE user_id = ?"))
        {
            ps.setInt(1, user_id);
            ps.executeUpdate();
        }
        catch(Exception e) {
            throw new SQLException(user_id + " is not found");
        }
        return 0;
    }


}

