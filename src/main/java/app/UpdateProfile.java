package app;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Profile settings: Update email, location, password + Delete account.
 *
 * Components:
 *   JFrame, JPanel, JLabel, JTextField, JPasswordField, JButton,
 *   GridBagLayout, BorderLayout, FlowLayout, ActionListener, JOptionPane.
 */
public class UpdateProfile extends JFrame {

    private final UserDAO userDAO;

    private JTextField     tfUsername;
    private JTextField     tfEmail;
    private JTextField     tfLocation;
    private JPasswordField pfOldPassword;
    private JPasswordField pfNewPassword;

    public UpdateProfile(UserDAO userDAO) {
        this.userDAO = userDAO;

        setTitle("Update Profile");
        setSize(520, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        User user = Session.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "No logged-in user found. Please log in first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Root
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(20, 24, 16, 24));
        setContentPane(root);

        // Title
        JLabel title = new JLabel("Profile Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        root.add(form, BorderLayout.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        // JTextField / JPasswordField
        tfUsername    = new JTextField(20);
        tfEmail       = new JTextField(20);
        tfLocation    = new JTextField(20);
        pfOldPassword = new JPasswordField(20);
        pfNewPassword = new JPasswordField(20);

        tfUsername.setEditable(false);
        tfUsername.setBackground(new Color(240, 240, 240));

        tfUsername.setText(nullToEmpty(user.getUserName()));
        tfEmail   .setText(nullToEmpty(user.getUserEmail()));
        tfLocation.setText(nullToEmpty(user.getUserLocation()));

        addFormRow(form, gc, 0, "Username:",     tfUsername);
        addFormRow(form, gc, 1, "Email:",         tfEmail);
        addFormRow(form, gc, 2, "Location:",      tfLocation);
        addFormRow(form, gc, 3, "Old Password:",  pfOldPassword);
        addFormRow(form, gc, 4, "New Password:",  pfNewPassword);

        JLabel hint = new JLabel("Leave password fields empty to keep current password.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        form.add(hint, gc);

        //Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnCancel  = new JButton("Cancel");
        JButton btnUpdate  = new JButton("Save Changes");
        JButton btnDelete  = new JButton("Delete Account");

        // Visual distinction for destructive action
        btnDelete.setForeground(new Color(180, 0, 0));

        buttons.add(btnRefresh);
        buttons.add(btnCancel);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        root.add(buttons, BorderLayout.SOUTH);

        //ActionListeners
        btnCancel .addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> refreshFromDb());
        btnUpdate .addActionListener(e -> performUpdate());

        // Delete account — requires password confirmation
        btnDelete.addActionListener(e -> {
            User u = Session.getCurrentUser();
            if (u == null) return;

            // Ask for password before deleting
            JPasswordField pfConfirm = new JPasswordField(18);
            JPanel confirmPanel = new JPanel(new GridLayout(2, 1, 6, 6));
            confirmPanel.add(new JLabel("Enter your password to confirm deletion:"));
            confirmPanel.add(pfConfirm);

            int result = JOptionPane.showConfirmDialog(this, confirmPanel,
                    "Confirm Account Deletion",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String pass = new String(pfConfirm.getPassword());
            if (!org.mindrot.jbcrypt.BCrypt.checkpw(pass, u.getUserPasswordHash())) {
                JOptionPane.showMessageDialog(this, "Incorrect password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                userDAO.deleteUser(u.getUserId());
                Session.setCurrentUser(null);
                JOptionPane.showMessageDialog(this,
                        "Account deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // Return to login
                new LoginPage(userDAO);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    //Update logic

    private void performUpdate() {
        User user = Session.getCurrentUser();
        if (user == null) return;

        String newEmail    = tfEmail   .getText().trim();
        String newLocation = tfLocation.getText().trim();
        String oldPass     = new String(pfOldPassword.getPassword());
        String newPass     = new String(pfNewPassword.getPassword());

        if (newEmail.isEmpty() || !newEmail.contains("@") || !newEmail.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean changed = false;

            if (!newEmail.equals(nullToEmpty(user.getUserEmail()))) {
                userDAO.updateEmail(user.getUserId(), newEmail);
                user.setEmail(newEmail);
                changed = true;
            }
            if (!newLocation.equals(nullToEmpty(user.getUserLocation()))) {
                userDAO.updateLocation(user.getUserId(), newLocation);
                user.setUserLocation(newLocation);
                changed = true;
            }
            if (!newPass.isEmpty()) {
                if (oldPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter your old password to set a new one.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!org.mindrot.jbcrypt.BCrypt.checkpw(oldPass, user.getUserPasswordHash())) {
                    JOptionPane.showMessageDialog(this,
                            "Old password is incorrect.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (newPass.length() < 6) {
                    JOptionPane.showMessageDialog(this,
                            "New password must be at least 6 characters.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                userDAO.updatePassword(user.getUserId(), newPass);
                changed = true;
            }

            if (!changed) {
                JOptionPane.showMessageDialog(this, "Nothing was changed.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            pfOldPassword.setText("");
            pfNewPassword.setText("");
            JOptionPane.showMessageDialog(this, "Profile updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshFromDb() {
        User user = Session.getCurrentUser();
        if (user == null) return;
        try {
            User fresh = userDAO.getById(user.getUserId());
            if (fresh == null) {
                JOptionPane.showMessageDialog(this, "User not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Session.setCurrentUser(fresh);
            tfUsername.setText(nullToEmpty(fresh.getUserName()));
            tfEmail   .setText(nullToEmpty(fresh.getUserEmail()));
            tfLocation.setText(nullToEmpty(fresh.getUserLocation()));
            pfOldPassword.setText("");
            pfNewPassword.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Helpers

    private static void addFormRow(JPanel form, GridBagConstraints gc,
                                   int row, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.gridwidth = 1;
        form.add(lbl, gc);
        gc.gridx = 1; gc.weightx = 1;
        form.add(field, gc);
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
