package app;

import dao.OrderDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Shows a user's order history and lets them delete orders.
 * Read getOrdersByUser() uses INNER JOIN (orders + restaurant)
 * Delete deleteOrder() removes order + order_items
 *
 * Components :
 *   JFrame, JPanel, JLabel, JButton, JList, JScrollPane,
 *   DefaultListModel, BorderLayout, FlowLayout,
 *   ActionListener, JOptionPane.
 */
public class MyOrders extends JFrame {

    private final OrderDAO orderDAO = new OrderDAO();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final java.util.List<Integer> orderIds = new java.util.ArrayList<>();
    private JList<String> orderList;
    private JLabel lblStatus;

    public MyOrders() {
        setTitle("My Orders");
        setSize(760, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        //NORTH
        JLabel lblTitle = new JLabel("My Order History");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        root.add(lblTitle, BorderLayout.NORTH);

        //CENTRE: JList
        orderList = new JList<>(listModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        orderList.setVisibleRowCount(12);
        JScrollPane scroll = new JScrollPane(orderList);
        scroll.setBorder(BorderFactory.createTitledBorder("Orders (newest first)"));
        root.add(scroll, BorderLayout.CENTER);

        //SOUTH
        JPanel southPanel = new JPanel(new BorderLayout(6, 6));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete  = new JButton("Delete Order");

        btnRow.add(btnRefresh);
        btnRow.add(btnDelete);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.GRAY);

        southPanel.add(btnRow,    BorderLayout.CENTER);
        southPanel.add(lblStatus, BorderLayout.SOUTH);
        root.add(southPanel, BorderLayout.SOUTH);

        //ActionListeners
        btnRefresh.addActionListener(e -> loadOrders());

        btnDelete.addActionListener(e -> {
            int idx = orderList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(this, "Please select an order first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = orderIds.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete order #" + orderId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                orderDAO.deleteOrder(orderId);
                lblStatus.setText("Order #" + orderId + " deleted.");
                loadOrders();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        User user = Session.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.",
                    "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }
        listModel.clear();
        orderIds.clear();
        try {
            List<String> orders = orderDAO.getOrdersByUser(user.getUserId());
            if (orders.isEmpty()) {
                lblStatus.setText("No orders found.");
                listModel.addElement("(No orders yet)");
            } else {
                // parse order id from each line
                for (String line : orders) {
                    listModel.addElement(line);
                    int id = Integer.parseInt(line.substring(1, line.indexOf(' ')));
                    orderIds.add(id);
                }
                lblStatus.setText(orders.size() + " order(s) found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load orders: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
