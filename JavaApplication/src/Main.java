import model.User;
import service.*;
import service.impl.*;
import menu.GuestMenuHandler;
import menu.ReceptionistMenuHandler;
import menu.AdminMenuHandler;
import menu.HousekeepingMenuHandler;
import util.UIComponents;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Main {
    private static final UserService userService = new UserServiceImpl();
    private static JFrame mainFrame;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                UIComponents.handleException(null, e, "Failed to set system look and feel");
            }
            createAndShowLoginWindow();
        });
    }

    private static void createAndShowLoginWindow() {
        try {
            mainFrame = new JFrame("Hotel Management System - Login");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(UIComponents.DIALOG_SIZE);
            mainFrame.setLocationRelativeTo(null);

            JPanel mainPanel = UIComponents.createMainPanel();

            // Title
            mainPanel.add(UIComponents.createTitleLabel("Select User to Login"), BorderLayout.NORTH);

            // User List
            List<User> users = userService.findAll();
            DefaultListModel<UserListItem> listModel = new DefaultListModel<>();
            for (User user : users) {
                listModel.addElement(new UserListItem(user));
            }

            JList<UserListItem> userList = new JList<>(listModel);
            userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(userList);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Login Button
            JButton loginButton = UIComponents.createStyledButton("Login");
            loginButton.addActionListener(e -> {
                UserListItem selectedItem = userList.getSelectedValue();
                if (selectedItem != null) {
                    try {
                        userService.login(selectedItem.getUser().getUserId());
                        showRoleSpecificWindow();
                    } catch (Exception ex) {
                        UIComponents.handleException(mainFrame, ex, "Failed to login");
                    }
                } else {
                    UIComponents.showWarning(mainFrame, "Please select a user");
                }
            });

            mainPanel.add(loginButton, BorderLayout.SOUTH);
            mainFrame.add(mainPanel);
            mainFrame.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(null, e, "Failed to create login window");
        }
    }

    private static void showRoleSpecificWindow() {
        try {
            User currentUser = userService.getCurrentUser();
            String userRole = userService.getCurrentRole();
            mainFrame.dispose();
            JFrame roleFrame = new JFrame("Hotel Management System - " + userRole);
            roleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            roleFrame.setSize(600, 400);
            roleFrame.setLocationRelativeTo(null);

            JPanel panel = UIComponents.createMainPanel();

            panel.add(UIComponents.createTitleLabel("Welcome, " + currentUser.getFirstName() + "!"), 
                BorderLayout.NORTH);

            // Role-specific content panel
            JPanel contentPanel = new JPanel();
            switch (userRole) {
                case "GUEST":
                    setupGuestPanel(contentPanel);
                    break;
                case "ADMINISTRATOR":
                    setupAdminPanel(contentPanel);
                    break;
                case "RECEPTIONIST":
                    setupReceptionistPanel(contentPanel);
                    break;
                case "HOUSEKEEPING":
                    setupHousekeepingPanel(contentPanel);
                    break;
                default:
                    UIComponents.showError(roleFrame, "Invalid user role: " + userRole);
                    return;
            }
            panel.add(contentPanel, BorderLayout.CENTER);

            // Logout Button
            JButton logoutButton = UIComponents.createStyledButton("Logout");
            logoutButton.addActionListener(e -> {
                try {
                    if (UIComponents.showConfirmDialog(roleFrame, "Are you sure you want to logout?", "Confirm Logout")) {
                        userService.logout();
                        roleFrame.dispose();
                        createAndShowLoginWindow();
                    }
                } catch (Exception ex) {
                    UIComponents.handleException(roleFrame, ex, "Failed to logout");
                }
            });
            panel.add(logoutButton, BorderLayout.SOUTH);

            roleFrame.add(panel);
            roleFrame.setVisible(true);
        } catch (Exception e) {
            UIComponents.handleException(mainFrame, e, "Failed to show role-specific window");
        }
    }

    private static void setupGuestPanel(JPanel panel) {
        GuestMenuHandler guestMenuHandler = new GuestMenuHandler((JFrame) SwingUtilities.getWindowAncestor(panel));
        panel.setLayout(new BorderLayout());
        panel.add(guestMenuHandler.getMainPanel(), BorderLayout.CENTER);
    }

    private static void setupAdminPanel(JPanel panel) {
        AdminMenuHandler adminMenuHandler = new AdminMenuHandler((JFrame) SwingUtilities.getWindowAncestor(panel));
        panel.setLayout(new BorderLayout());
        panel.add(adminMenuHandler.getMainPanel(), BorderLayout.CENTER);
    }

    private static void setupReceptionistPanel(JPanel panel) {
        ReceptionistMenuHandler receptionistMenuHandler = new ReceptionistMenuHandler((JFrame) SwingUtilities.getWindowAncestor(panel));
        panel.setLayout(new BorderLayout());
        panel.add(receptionistMenuHandler.getMainPanel(), BorderLayout.CENTER);
    }

    private static void setupHousekeepingPanel(JPanel panel) {
        HousekeepingMenuHandler housekeepingMenuHandler = new HousekeepingMenuHandler((JFrame) SwingUtilities.getWindowAncestor(panel));
        panel.setLayout(new BorderLayout());
        panel.add(housekeepingMenuHandler.getMainPanel(), BorderLayout.CENTER);
    }

    // Helper class for JList items
    private static class UserListItem {
        private final User user;

        public UserListItem(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public String toString() {
            return user.getFirstName() + " " + user.getLastName();
        }
    }
}