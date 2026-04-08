package dao;
import java.sql.*;

import app.Session;
import util.DB;
import model.User;
import model.Restaurant;
import org.mindrot.jbcrypt.BCrypt;


public class UserDAO {
    //System methods
    //login user method
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, user_name, password_hash, user_location, role from user where user_name = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String hash = rs.getString("password_hash");
                if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, hash)) return null;

                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("user_name"));
                user.setRole(rs.getBoolean("role"));
                user.setUserPasswordHash(rs.getString("password_hash"));
                user.setUserLocation(rs.getString("user_location"));
                Session.setCurrentUser(user);
                return user;
            }
        }

    }

    //signup user method
    public int signUp(User user) throws SQLException {
        String sql = "INSERT INTO user (user_name, password_hash, user_email, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getUserPasswordHash());
            ps.setString(3, user.getUserEmail());
            ps.setBoolean(4, user.isAdmin());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }



    //Checking Methods
    //Method that checks status for admins
    public boolean checkStatus(String userId) throws SQLException {
        String sql = "SELECT role FROM user WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("role");
                }

            }
        }
        return false;
    }

    public String checkLocation(String user_id) throws SQLException {
        String sql = "SELECT user_location FROM user WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_location");
                }
            }
        }
        return null;
    }





    // get user method
    public User getById(int userId) throws SQLException {
        String sql = "SELECT user_name, user_email, user_location FROM user WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("user_name"));
                    user.setEmail(rs.getString("user_email"));
                    user.setUserLocation(rs.getString("user_location"));
                    return user;
                }
            }
        }
        return null;
    }




    //update methods
    public void updateUsername(int userId, String userName) throws SQLException {
        String sql = "UPDATE user SET user_name = ? WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
        return;
    }

    public void updateEmail(int userId, String email) throws SQLException {
        String sql = "UPDATE user SET user_email = ? WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
        return;
    }

    public void updateLocation(int userId, String location) throws SQLException {
        String sql = "UPDATE user SET user_location = ? WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, location);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
        return;
    }

    public void updatePassword(int userId, String newPasswordPlain) throws SQLException {
        String sql = "UPDATE user SET password_hash = ? WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String userPasswordHash = BCrypt.hashpw(newPasswordPlain, BCrypt.gensalt());
            ps.setString(1, userPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
        return;
    }


    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

}
