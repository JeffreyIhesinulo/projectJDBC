package app;

import dao.RestaurantDAO;
import model.Restaurant;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Components :
 *   JFrame, JPanel, JLabel, JButton, JList, JScrollPane,
 *   DefaultListModel, BorderLayout, FlowLayout, GridLayout,
 *   ActionListener, JOptionPane, JTextField, JCheckBox.
 */
public class MyRestaurants extends JFrame {

    private final RestaurantDAO restaurantDAO = new RestaurantDAO();
    private final DefaultListModel<Restaurant> listModel = new DefaultListModel<>();
    private JList<Restaurant> restaurantList;
    private JLabel lblStatus;

    public MyRestaurants() {
        setTitle("My Restaurants");
        setSize(960, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        //NORTH
        JLabel lblTitle = new JLabel("My Restaurants – Manage");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        root.add(lblTitle, BorderLayout.NORTH);

        // CENTRE: JList
        restaurantList = new JList<>(listModel);
        restaurantList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        restaurantList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        restaurantList.setVisibleRowCount(12);

        JScrollPane scrollPane = new JScrollPane(restaurantList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Restaurant List"));
        root.add(scrollPane, BorderLayout.CENTER);

        //SOUTH: buttons
        JPanel southPanel = new JPanel(new BorderLayout(6, 6));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd     = new JButton("Add Restaurant");      // CREATE
        JButton btnEdit    = new JButton("Edit");                 // UPDATE
        JButton btnToggle  = new JButton("Toggle Status");        // UPDATE
        JButton btnDelete  = new JButton("Delete Restaurant");    // DELETE

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnToggle);
        btnPanel.add(btnDelete);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.GRAY);

        southPanel.add(btnPanel,  BorderLayout.CENTER);
        southPanel.add(lblStatus, BorderLayout.SOUTH);
        root.add(southPanel, BorderLayout.SOUTH);

        //ActionListeners
        btnRefresh.addActionListener(e -> loadRestaurants());

        btnAdd.addActionListener(e -> showAddDialog());

        btnEdit.addActionListener(e -> showEditDialog());

        btnToggle.addActionListener(e -> {
            Restaurant sel = getSelected();
            if (sel == null) return;
            try {
                restaurantDAO.updateRestaurantStatus(sel.getRestaurantId(),
                        !sel.isRestaurantStatus());
                lblStatus.setText("Status updated: " + sel.getRestaurantName());
                loadRestaurants();
            } catch (Exception ex) {
                error(ex);
            }
        });

        btnDelete.addActionListener(e -> {
            Restaurant sel = getSelected();
            if (sel == null) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete restaurant \"" + sel.getRestaurantName() + "\"?\n" +
                    "This will also remove its menu items.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                restaurantDAO.deleteRestaurant(sel.getRestaurantId());
                lblStatus.setText("Deleted: " + sel.getRestaurantName());
                loadRestaurants();
            } catch (Exception ex) {
                error(ex);
            }
        });

        loadRestaurants();
    }

    //Data loading

    private void loadRestaurants() {
        User user = Session.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.",
                    "Not Logged In", JOptionPane.ERROR_MESSAGE);
            return;
        }
        listModel.clear();
        try {
            List<Restaurant> list = restaurantDAO.getRestaurantsByAdminId(user.getUserId());
            list.forEach(listModel::addElement);
            lblStatus.setText(list.isEmpty()
                    ? "No restaurants found for your account."
                    : list.size() + " restaurant(s) loaded.");
        } catch (Exception ex) {
            error(ex);
        }
    }

    //Dialogs

    private void showAddDialog() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        JTextField tfName     = new JTextField();
        JTextField tfType     = new JTextField();
        JTextField tfLocation = new JTextField();
        JCheckBox  cbActive   = new JCheckBox("Active", true);
        form.add(new JLabel("Name:"));     form.add(tfName);
        form.add(new JLabel("Type:"));     form.add(tfType);
        form.add(new JLabel("Location:")); form.add(tfLocation);
        form.add(new JLabel("Status:"));   form.add(cbActive);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Restaurant",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return;

        String name = tfName.getText().trim();
        String type = tfType.getText().trim();
        String loc  = tfLocation.getText().trim();
        if (name.isEmpty() || type.isEmpty() || loc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = Session.getCurrentUser();
        if (user == null) return;
        try {
            restaurantDAO.addRestaurant(name, type, loc,
                    cbActive.isSelected(), user.getUserId());
            lblStatus.setText("Added: " + name);
            loadRestaurants();
        } catch (Exception ex) { error(ex); }
    }

    private void showEditDialog() {
        Restaurant sel = getSelected();
        if (sel == null) return;

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField tfName     = new JTextField(sel.getRestaurantName());
        JTextField tfType     = new JTextField(sel.getRestaurantType());
        JTextField tfLocation = new JTextField(sel.getRestaurantLocation());
        form.add(new JLabel("Name:"));     form.add(tfName);
        form.add(new JLabel("Type:"));     form.add(tfType);
        form.add(new JLabel("Location:")); form.add(tfLocation);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Restaurant",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return;

        try {
            String name = tfName.getText().trim();
            String type = tfType.getText().trim();
            String loc  = tfLocation.getText().trim();
            if (name.isEmpty() || type.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            restaurantDAO.updateRestaurantName(sel.getRestaurantId(), name);
            restaurantDAO.updateRestaurantType(sel.getRestaurantId(), type);
            restaurantDAO.updateRestaurantLocation(sel.getRestaurantId(), loc);
            lblStatus.setText("Updated: " + name);
            loadRestaurants();
        } catch (Exception ex) { error(ex); }
    }

    // Helpers

    private Restaurant getSelected() {
        Restaurant sel = restaurantList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
        return sel;
    }

    private void error(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyRestaurants().setVisible(true));
    }
}
