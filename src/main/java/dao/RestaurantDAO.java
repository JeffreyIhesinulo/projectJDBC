package dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import model.Restaurant;
import util.DB;

public class RestaurantDAO {

    //Read

    /** All restaurants for the given admin user. */
    public List<Restaurant> getRestaurantsByAdminId(int adminUserId) throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT restaurant_id, restaurant_name, restaurant_type, " +
                     "restaurant_location, restaurant_status, admin_user_id " +
                     "FROM restaurant WHERE admin_user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, adminUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Open restaurants at a given location (instance method, not static). */
    public List<Restaurant> getRestaurantsByLocation(String location) throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT restaurant_id, restaurant_name, restaurant_type, " +
                     "restaurant_location, restaurant_status, admin_user_id " +
                     "FROM restaurant " +
                     "WHERE restaurant_location = ? AND restaurant_status = true";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, location);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    //Create

    public void addRestaurant(String name, String type, String location,
                              boolean status, int adminUserId) throws SQLException {
        String sql = "INSERT INTO restaurant " +
                     "(restaurant_name, restaurant_type, restaurant_location, restaurant_status, admin_user_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, location);
            ps.setBoolean(4, status);
            ps.setInt(5, adminUserId);
            ps.executeUpdate();
        }
    }

    //Update

    public void updateRestaurantName(int restaurantId, String name) throws SQLException {
        exec("UPDATE restaurant SET restaurant_name = ? WHERE restaurant_id = ?", name, restaurantId);
    }

    public void updateRestaurantStatus(int restaurantId, boolean status) throws SQLException {
        String sql = "UPDATE restaurant SET restaurant_status = ? WHERE restaurant_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, restaurantId);
            ps.executeUpdate();
        }
    }

    public void updateRestaurantLocation(int restaurantId, String location) throws SQLException {
        exec("UPDATE restaurant SET restaurant_location = ? WHERE restaurant_id = ?", location, restaurantId);
    }

    public void updateRestaurantType(int restaurantId, String type) throws SQLException {
        exec("UPDATE restaurant SET restaurant_type = ? WHERE restaurant_id = ?", type, restaurantId);
    }

    //Helpers

    private void exec(String sql, String strParam, int idParam) throws SQLException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, strParam);
            ps.setInt(2, idParam);
            ps.executeUpdate();
        }
    }

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        Restaurant r = new Restaurant();
        r.setRestaurantId(rs.getInt("restaurant_id"));
        r.setRestaurantName(rs.getString("restaurant_name"));
        r.setRestaurantType(rs.getString("restaurant_type"));
        r.setRestaurantLocation(rs.getString("restaurant_location"));
        r.setRestaurantStatus(rs.getBoolean("restaurant_status"));
        r.setAdminId(rs.getInt("admin_user_id"));
        return r;
    }

    //Delete

    public void deleteRestaurant(int restaurantId) throws SQLException {
        String sql = "DELETE FROM restaurant WHERE restaurant_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            ps.executeUpdate();
        }
    }

}
