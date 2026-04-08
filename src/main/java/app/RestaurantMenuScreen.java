package app;

import dao.MenuDAO;
import dao.OrderDAO;
import model.CartItem;
import model.MenuItem;
import model.Restaurant;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the menu for a selected restaurant.
 * Loads real data from the menu table using MenuDAO (inner join with restaurant).
 * Place Order saves to the orders + order_items tables with OrderDAO.
 *
 * Components:
 *   JFrame, JPanel, JLabel, JButton, JList, JScrollPane, JTextArea,
 *   DefaultListModel, BorderLayout, GridLayout, FlowLayout,
 *   ActionListener, ListSelectionListener, JOptionPane.
 */
public class RestaurantMenuScreen extends JFrame {

    private final Restaurant restaurant;
    private final MenuDAO  menuDAO  = new MenuDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final DefaultListModel<String> menuModel = new DefaultListModel<>();
    private final List<MenuItem> items = new ArrayList<>();

    private double cartTotal = 0.0;
    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<Double> cartPrices = new ArrayList<>();

    private JLabel    lblItemDetail;
    private JTextArea taCartSummary;
    private JLabel    lblTotal;

    public RestaurantMenuScreen(Restaurant restaurant) {
        this.restaurant = restaurant;
        setTitle("Menu – " + restaurant.getRestaurantName());
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Root
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        //NORTH
        JLabel lblHeader = new JLabel(
                restaurant.getRestaurantName()
                + "   |   " + restaurant.getRestaurantType()
                + "   |   " + restaurant.getRestaurantLocation());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(lblHeader, BorderLayout.NORTH);

        //CENTRE: GridLayout 1×2
        JPanel centre = new JPanel(new GridLayout(1, 2, 12, 0));

        // Left: menu JList
        JList<String> menuList = new JList<>(menuModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        menuList.setVisibleRowCount(10);
        JScrollPane menuScroll = new JScrollPane(menuList);
        menuScroll.setBorder(BorderFactory.createTitledBorder("Menu Items"));
        centre.add(menuScroll);

        // Right: detail + admin buttons
        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));

        lblItemDetail = new JLabel("Select an item to see details.", SwingConstants.CENTER);
        lblItemDetail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailPanel.add(lblItemDetail, BorderLayout.CENTER);

        // Button panel (admin can add/edit/delete menu items)
        JPanel itemBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        JButton btnAddToCart  = new JButton("Add to Cart");
        JButton btnAddItem    = new JButton("Add Dish");
        JButton btnEditItem   = new JButton("Edit Dish");
        JButton btnDeleteItem = new JButton("Delete Dish");

        itemBtns.add(btnAddToCart);

        User user = Session.getCurrentUser();
        if (user != null && user.getRole()) {   // only admin sees dish management
            itemBtns.add(btnAddItem);
            itemBtns.add(btnEditItem);
            itemBtns.add(btnDeleteItem);
        }
        detailPanel.add(itemBtns, BorderLayout.SOUTH);
        centre.add(detailPanel);
        root.add(centre, BorderLayout.CENTER);

        //SOUTH: cart
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setBorder(BorderFactory.createTitledBorder("Cart"));

        taCartSummary = new JTextArea(4, 30);
        taCartSummary.setEditable(false);
        taCartSummary.setFont(new Font("Monospaced", Font.PLAIN, 12));
        southPanel.add(new JScrollPane(taCartSummary), BorderLayout.CENTER);

        JPanel cartBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        lblTotal = new JLabel("Total: €0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton btnClear = new JButton("Clear Cart");
        JButton btnOrder = new JButton("Place Order");
        cartBtnRow.add(lblTotal);
        cartBtnRow.add(btnClear);
        cartBtnRow.add(btnOrder);
        southPanel.add(cartBtnRow, BorderLayout.SOUTH);
        root.add(southPanel, BorderLayout.SOUTH);

        //Load menu from DB
        loadMenu();

        //Event handlers

        // ListSelectionListener show detail
        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = menuList.getSelectedIndex();
                if (idx >= 0) {
                    MenuItem item = items.get(idx);
                    lblItemDetail.setText(String.format(
                            "<html><b>%s</b><br/>Price: €%.2f</html>",
                            item.getDish(), item.getPrice()));
                }
            }
        });

        // Add to Cart
        btnAddToCart.addActionListener(e -> {
            int idx = menuList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Please select an item first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MenuItem item = items.get(idx);
            cartItems.add(new CartItem(item.getMenuId(), 1));
            cartPrices.add(item.getPrice());
            cartTotal += item.getPrice();
            taCartSummary.append(String.format("%-28s  €%.2f%n",
                    item.getDish(), item.getPrice()));
            lblTotal.setText("Total: €" + String.format("%.2f", cartTotal));
        });

        // Clear Cart
        btnClear.addActionListener(e -> {
            cartItems.clear();
            cartPrices.clear();
            cartTotal = 0.0;
            taCartSummary.setText("");
            lblTotal.setText("Total: €0.00");
        });

        // Place Order — saves to DB
        btnOrder.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.",
                        "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User u = Session.getCurrentUser();
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Please log in first.",
                        "Not Logged In", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int orderId = orderDAO.placeOrder(cartItems,
                        u.getUserId(), restaurant.getRestaurantId(), cartPrices);
                JOptionPane.showMessageDialog(this,
                        "Order #" + orderId + " placed!\nTotal: €"
                                + String.format("%.2f", cartTotal),
                        "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
                cartItems.clear();
                cartPrices.clear();
                cartTotal = 0.0;
                taCartSummary.setText("");
                lblTotal.setText("Total: €0.00");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Order failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add Dish (admin only)
        btnAddItem.addActionListener(e -> {
            JTextField tfName  = new JTextField();
            JTextField tfPrice = new JTextField();
            JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
            form.add(new JLabel("Dish name:")); form.add(tfName);
            form.add(new JLabel("Price (€):"));  form.add(tfPrice);
            int r = JOptionPane.showConfirmDialog(this, form, "Add Dish",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dish name cannot be empty.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(tfPrice.getText().trim());
                if (price < 0) throw new NumberFormatException();
                menuDAO.addItem(name, price, restaurant.getRestaurantId());
                loadMenu();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price (e.g. 9.50).",
                        "Invalid Price", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit Dish (admin only)
        btnEditItem.addActionListener(e -> {
            int idx = menuList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Please select a dish first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MenuItem item = items.get(idx);
            JTextField tfName  = new JTextField(item.getDish());
            JTextField tfPrice = new JTextField(String.format("%.2f", item.getPrice()));
            JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
            form.add(new JLabel("Dish name:")); form.add(tfName);
            form.add(new JLabel("Price (€):"));  form.add(tfPrice);
            int r = JOptionPane.showConfirmDialog(this, form, "Edit Dish",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
            try {
                double price = Double.parseDouble(tfPrice.getText().trim());
                if (price < 0) throw new NumberFormatException();
                menuDAO.updateItem(item.getMenuId(), tfName.getText().trim(), price);
                loadMenu();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price (e.g. 9.50).",
                        "Invalid Price", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete Dish (admin only)
        btnDeleteItem.addActionListener(e -> {
            int idx = menuList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Please select a dish first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MenuItem item = items.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + item.getDish() + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                menuDAO.deleteItem(item.getMenuId());
                loadMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadMenu() {
        items.clear();
        menuModel.clear();
        try {
            List<MenuItem> loaded = menuDAO.getByRestaurant(restaurant.getRestaurantId());
            for (MenuItem item : loaded) {
                items.add(item);
                menuModel.addElement(String.format("%-28s  €%.2f",
                        item.getDish(), item.getPrice()));
            }
            if (loaded.isEmpty()) {
                menuModel.addElement("(No menu items yet)");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load menu: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
