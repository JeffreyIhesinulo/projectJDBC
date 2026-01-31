package app;

import DAO.UserDAO;
import Model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class JFrameEx {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Demo");


        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton getAllUsers = new JButton("Get All Users");
        JButton addUser = new JButton("Add User");
        JButton updateUser = new JButton("Update User");
        JButton deleteUser = new JButton("Delete User");
        JTextArea output = new JTextArea(20,60);
        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);

        panel.add(getAllUsers);
        panel.add(addUser);
        panel.add(deleteUser);
        panel.add(updateUser);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        getAllUsers.addActionListener(e -> {
            output.setText("");
            try
            {
                List<User> users = userDAO.getAllUsers();
                for (User user : users) {
                    output.append(user.toString() + "\n");
                }
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }

        });
        addUser.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog("Enter user name");
            if(userName == null)
            {
                JOptionPane.showMessageDialog(frame, "Operation canceled", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            userName = userName.trim();
            if(userName.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userEmail = JOptionPane.showInputDialog("Enter user email");
            if(userEmail == null || userEmail.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "Email cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

                try {
                    User user = new User(0, userName.trim(), userEmail.trim());
                    userDAO.addUser(user);
                    JOptionPane.showMessageDialog(frame, "Successfully added user");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            });






        updateUser.addActionListener(e -> {
            String stringUserId = (JOptionPane.showInputDialog("Enter user id"));
            if(stringUserId == null)
            {
                JOptionPane.showMessageDialog(frame, "Operation canceled", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if(stringUserId.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "User id cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            stringUserId = stringUserId.trim();
            int userId;
                try
                {
                    userId = Integer.parseInt(stringUserId);
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(frame, "Invalid user id", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            if(userId <= 0) return;

            boolean exists = false;
            try{
                for (User user : userDAO.getAllUsers()) {
                    if(user.getUserId() == userId){
                        exists = true;
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                return;
            }
            if(!exists)
            {
                JOptionPane.showMessageDialog(frame, "User doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userName = JOptionPane.showInputDialog("Enter user name");
            if(userName == null || userName.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userEmail = JOptionPane.showInputDialog("Enter user email");
            if(userEmail == null || userEmail.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "Email cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userId <= 0) return;

            try {
                User user = new User(userId, userName, userEmail);
                userDAO.updateUser(user);
                JOptionPane.showMessageDialog(frame, "Successfully updated user");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });





        deleteUser.addActionListener(e -> {
            String stringUserId = (JOptionPane.showInputDialog("Enter user id"));
            if(stringUserId == null)
            {
                JOptionPane.showMessageDialog(frame, "Operation canceled", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if(stringUserId.isEmpty())
            {
                JOptionPane.showMessageDialog(frame, "User id cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            stringUserId = stringUserId.trim();
            int userId;
            try{
                userId = Integer.parseInt(stringUserId);
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Invalid user id", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean exists = false;
            try {
                for (User user : userDAO.getAllUsers())
                {
                    if(user.getUserId() == userId){
                        exists = true;
                    }
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                return;
            }
            if(!exists)
            {
                JOptionPane.showMessageDialog(frame, "User doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                userDAO.deleteUser(userId);
                JOptionPane.showMessageDialog(frame, "Successfully deleted user");
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });
        frame.setVisible(true);
    }

}
