package app;

import dao.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Login / Register screen.
 * Components used: JFrame, JPanel, JLabel, JTextField, JPasswordField,
 * JButton, FlowLayout, GridBagLayout, BorderLayout, ActionListener,
 * JOptionPane
 * Two JButtons switch between login and register panels via CardLayout.
 */
public class LoginPage extends JFrame {

    private final UserDAO userDAO;

    //Login fields
    private JTextField    tfUsername;
    private JPasswordField pfPassword;

    //Register fields
    private JTextField    tfRegName;
    private JTextField    tfRegEmail;
    private JPasswordField pfRegPassword;

    private static final String CARD_LOGIN    = "LOGIN";
    private static final String CARD_REGISTER = "REGISTER";

    public LoginPage(UserDAO userDAO) {
        this.userDAO = userDAO;

        setTitle("FoodApp – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);


        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnShowLogin    = new JButton("Login");
        JButton btnShowRegister = new JButton("Register");
        topPanel.add(btnShowLogin);
        topPanel.add(btnShowRegister);
        root.add(topPanel, BorderLayout.NORTH);

        CardLayout cards = new CardLayout();
        JPanel cardPanel = new JPanel(cards);
        cardPanel.add(buildLoginPanel(),    CARD_LOGIN);
        cardPanel.add(buildRegisterPanel(), CARD_REGISTER);
        root.add(cardPanel, BorderLayout.CENTER);

        // Toggle button actions
        btnShowLogin   .addActionListener(e -> cards.show(cardPanel, CARD_LOGIN));
        btnShowRegister.addActionListener(e -> cards.show(cardPanel, CARD_REGISTER));

        setVisible(true);
    }

    //Login panel

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel lblTitle = new JLabel("Sign In", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(lblTitle, gc);

        gc.gridwidth = 1;

        // Username — JLabel + JTextField
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        panel.add(new JLabel("Username:"), gc);
        tfUsername = new JTextField(18);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(tfUsername, gc);

        // Password — JLabel + JPasswordField
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        panel.add(new JLabel("Password:"), gc);
        pfPassword = new JPasswordField(18);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(pfPassword, gc);

        // Login button
        JButton btnLogin = new JButton("Login");
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        panel.add(btnLogin, gc);

        // ActionListener on button and password field
        ActionListener loginAction = e -> performLogin();
        btnLogin  .addActionListener(loginAction);
        pfPassword.addActionListener(loginAction);
        tfUsername.addActionListener(loginAction);

        return panel;
    }

    private void performLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                pfPassword.setText("");
                return;
            }
            String msg = user.getRole()
                    ? "Welcome, Admin " + user.getUserName() + "!"
                    : "Welcome, "       + user.getUserName() + "!";
            JOptionPane.showMessageDialog(this, msg, "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainScreen(userDAO).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Register panel
    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(lblTitle, gc);

        gc.gridwidth = 1;

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        panel.add(new JLabel("Username:"), gc);
        tfRegName = new JTextField(18);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(tfRegName, gc);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        panel.add(new JLabel("Email:"), gc);
        tfRegEmail = new JTextField(18);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(tfRegEmail, gc);

        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0;
        panel.add(new JLabel("Password:"), gc);
        pfRegPassword = new JPasswordField(18);
        gc.gridx = 1; gc.weightx = 1;
        panel.add(pfRegPassword, gc);

        // Hint label
        JLabel hint = new JLabel("Password must be at least 6 characters.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(Color.GRAY);
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        panel.add(hint, gc);

        JButton btnRegister = new JButton("Create Account");
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        panel.add(btnRegister, gc);

        ActionListener regAction = e -> performRegister();
        btnRegister  .addActionListener(regAction);
        pfRegPassword.addActionListener(regAction);

        return panel;
    }

    private void performRegister() {
        String name     = tfRegName.getText().trim();
        String email    = tfRegEmail.getText().trim();
        String password = new String(pfRegPassword.getPassword());

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(0, name, hash, email, false);
            userDAO.signUp(user);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully! You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            tfRegName.setText("");
            tfRegEmail.setText("");
            pfRegPassword.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage(new UserDAO()));
    }
}
