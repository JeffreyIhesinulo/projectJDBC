package dao;

import java.sql.*;
import model.OrderItem;
import util.DB;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import model.CartItem;

/**
 * CRUD for orders and order_items tables.
 * Real DB schema:
 *   order_items: order_item_id, order_id, dish_id, qty, unit_price
 *   orders:      order_id, user_id, restaurant_id, total_price, created_at
 */
public class OrderDAO {

    //Internal helpers

    private int createOrder(Connection con, int userId, int restaurantId) throws SQLException {
        String sql = "INSERT INTO orders (user_id, restaurant_id, total_price, created_at) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, restaurantId);
            ps.setDouble(3, 0.0);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            if (ps.executeUpdate() == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    private boolean addItem(Connection con, int orderId, int dishId,
                            int qty, double unitPrice) throws SQLException {
        // order_items uses dish_id and unit_price columns
        String sql = "INSERT INTO order_items (order_id, dish_id, qty, unit_price) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, dishId);
            ps.setInt(3, qty);
            ps.setDouble(4, unitPrice);
            return ps.executeUpdate() == 1;
        }
    }

    private boolean updateTotal(Connection con, int orderId) throws SQLException {
        // Use unit_price stored in order_items (no JOIN needed, but using INNER JOIN
        // with menu to demonstrate the join as required by the CA)
        String sql =
            "UPDATE orders " +
            "SET total_price = (" +
            "  SELECT COALESCE(SUM(oi.qty * m.price), 0) " +
            "  FROM order_items oi " +
            "  INNER JOIN menu m ON m.dish_id = oi.dish_id " +
            "  WHERE oi.order_id = ?" +
            ") WHERE order_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, orderId);
            return ps.executeUpdate() == 1;
        }
    }

    //Public: place a full order (transaction)

    public int placeOrder(List<CartItem> cart, int userId, int restaurantId,
                          List<Double> prices) throws SQLException {
        if (cart == null || cart.isEmpty()) return -1;

        Connection con = null;
        try {
            con = DB.getConnection();
            con.setAutoCommit(false);

            int orderId = createOrder(con, userId, restaurantId);
            if (orderId == -1) throw new SQLException("Failed to create order");

            for (int i = 0; i < cart.size(); i++) {
                CartItem item = cart.get(i);
                double unitPrice = (prices != null && i < prices.size()) ? prices.get(i) : 0.0;
                if (item.getQty() <= 0) throw new SQLException("Invalid qty: " + item.getQty());
                if (!addItem(con, orderId, item.getMenuId(), item.getQty(), unitPrice))
                    throw new SQLException("Failed to add order item");
            }

            updateTotal(con, orderId);
            con.commit();
            return orderId;

        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignored) {} }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                try { con.close();             } catch (SQLException ignored) {}
            }
        }
    }

    //READ: orders for a user (INNER JOIN)

    public List<String> getOrdersByUser(int userId) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql =
            "SELECT o.order_id, r.restaurant_name, o.total_price, o.created_at " +
            "FROM orders o " +
            "INNER JOIN restaurant r ON o.restaurant_id = r.restaurant_id " +
            "WHERE o.user_id = ? " +
            "ORDER BY o.created_at DESC";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(String.format("#%d  %-22s  €%.2f  %s",
                            rs.getInt("order_id"),
                            rs.getString("restaurant_name"),
                            rs.getDouble("total_price"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                                    .toString().replace("T", "  ").substring(0, 19)));
                }
            }
        }
        return list;
    }

    //READ: get order IDs for a user (for delete selection)

    public List<Integer> getOrderIdsByUser(int userId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT order_id FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("order_id"));
            }
        }
        return ids;
    }

    //DELETE

    public void deleteOrder(int orderId) throws SQLException {
        Connection con = null;
        try {
            con = DB.getConnection();
            con.setAutoCommit(false);
            // order_items cascade deletes with the order (FK ON DELETE CASCADE)
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM order_items WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM orders WHERE order_id = ?")) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            if (con != null) { try { con.rollback(); } catch (SQLException ignored) {} }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (SQLException ignored) {}
                try { con.close();             } catch (SQLException ignored) {}
            }
        }
    }
}
