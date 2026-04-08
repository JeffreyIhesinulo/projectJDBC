package app;

import dao.RestaurantDAO;
import dao.UserDAO;
import model.Restaurant;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main application screen.
 * "My Restaurants" button is shown only to admin users.
 *
 * Components :
 *   JFrame, JPanel, JLabel, JButton, JList, JScrollPane, JOptionPane,
 *   DefaultListModel, BorderLayout, FlowLayout,
 *   ActionListener, MouseListener (MouseAdapter).
 */
public class MainScreen extends JFrame {

    private final UserDAO       userDAO;
    private final RestaurantDAO restaurantDAO;
    private final DefaultListModel<Restaurant> listModel;

    public MainScreen(UserDAO userDAO) {
        this.userDAO       = userDAO;
        this.restaurantDAO = new RestaurantDAO();
        this.listModel     = new DefaultListModel<>();

        setTitle("FoodApp – Main");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel bg = new BackgroundPanel("/images/background.png");
        bg.setBackground(new Color(26, 26, 46));
        bg.setLayout(new BorderLayout(12, 12));
        setContentPane(bg);

        // Top nav bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setOpaque(false);

        JButton btnNearMe   = makeNavButton("Restaurants Near Me");
        JButton btnProfile  = makeNavButton("Update Profile");
        JButton btnMyOrders = makeNavButton("My Orders");

        topPanel.add(btnNearMe);
        topPanel.add(btnProfile);
        topPanel.add(btnMyOrders);

        // "My Restaurants" only for admins
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && currentUser.getRole()) {
            JButton btnMyRest = makeNavButton("My Restaurants");
            topPanel.add(btnMyRest);
            btnMyRest.addActionListener(e -> new MyRestaurants().setVisible(true));
        }

        // Centre
        JPanel centrePanel = new JPanel(new BorderLayout(0, 6));
        centrePanel.setOpaque(false);
        centrePanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel lblHint = new JLabel(
                "Double-click a restaurant to open its menu.",
                SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblHint.setForeground(new Color(180, 180, 180));

        JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(150, 150, 150));

        // JList
        JList<Restaurant> restaurantList = new JList<>(listModel);
        restaurantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        restaurantList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        restaurantList.setBackground(new Color(255, 255, 255, 20));
        restaurantList.setForeground(Color.WHITE);
        restaurantList.setSelectionBackground(new Color(30, 100, 200));
        restaurantList.setSelectionForeground(Color.WHITE);

        // JScrollPane
        JScrollPane scrollPane = new JScrollPane(restaurantList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 60));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40)));

        centrePanel.add(lblHint,    BorderLayout.NORTH);
        centrePanel.add(scrollPane, BorderLayout.CENTER);
        centrePanel.add(lblStatus,  BorderLayout.SOUTH);

        bg.add(topPanel,    BorderLayout.NORTH);
        bg.add(centrePanel, BorderLayout.CENTER);

        // MouseListener: double-click
        restaurantList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    Restaurant sel = restaurantList.getSelectedValue();
                    if (sel != null) new RestaurantMenuScreen(sel).setVisible(true);
                }
            }
        });

        //ActionListeners
        btnNearMe  .addActionListener(e -> loadRestaurants(lblStatus));
        btnProfile .addActionListener(e -> new UpdateProfile(userDAO).setVisible(true));
        btnMyOrders.addActionListener(e -> new MyOrders().setVisible(true));
    }

    private void loadRestaurants(JLabel lblStatus) {
        User user = Session.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.",
                    "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (user.getUserLocation() == null || user.getUserLocation().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please set your location in Update Profile.",
                    "Location Missing", JOptionPane.WARNING_MESSAGE);
            new UpdateProfile(userDAO).setVisible(true);
            return;
        }
        try {
            listModel.clear();
            List<Restaurant> restaurants =
                    restaurantDAO.getRestaurantsByLocation(user.getUserLocation());
            if (restaurants.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No restaurants found in your area.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("No restaurants found in: " + user.getUserLocation());
                return;
            }
            restaurants.forEach(listModel::addElement);
            lblStatus.setText(restaurants.size() + " restaurant(s) found in: "
                    + user.getUserLocation());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton makeNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(30, 30, 30));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80)),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen(new UserDAO()).setVisible(true));
    }
}
