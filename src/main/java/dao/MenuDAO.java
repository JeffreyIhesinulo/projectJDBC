package dao;

import model.MenuItem;
import util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD for the menu table.
 * getByRestaurant() uses an INNER JOIN over menu + restaurant.
 * Column name in DB: dish_id (not menu_id).
 */
public class MenuDAO {

    //READ (with INNER JOIN)

    public List<MenuItem> getByRestaurant(int restaurantId) throws SQLException {
        List<MenuItem> list = new ArrayList<>();
        String sql =
            "SELECT m.dish_id, m.dish_name, m.price, r.restaurant_name " +
            "FROM menu m " +
            "INNER JOIN restaurant r ON m.restaurant_id = r.restaurant_id " +
            "WHERE m.restaurant_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MenuItem(
                            rs.getInt("dish_id"),
                            rs.getString("dish_name"),
                            rs.getDouble("price")));
                }
            }
        }
        return list;
    }

    //CREATE

    public void addItem(String dishName, double price, int restaurantId) throws SQLException {
        String sql = "INSERT INTO menu (dish_name, price, restaurant_id) VALUES (?, ?, ?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dishName);
            ps.setDouble(2, price);
            ps.setInt(3, restaurantId);
            ps.executeUpdate();
        }
    }

    //UPDATE

    public void updateItem(int dishId, String dishName, double price) throws SQLException {
        String sql = "UPDATE menu SET dish_name = ?, price = ? WHERE dish_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dishName);
            ps.setDouble(2, price);
            ps.setInt(3, dishId);
            ps.executeUpdate();
        }
    }

    //DELETE

    public void deleteItem(int dishId) throws SQLException {
        String sql = "DELETE FROM menu WHERE dish_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }
}
