package com.project.gui;

import com.project.models.User;
import com.project.services.AuthService;
import com.project.validation.ValidationUtil;
import javax.swing.*;
import java.awt.*;

/**
 * MainWindow - Primary application window
 * Manages LoginPanel, RegisterPanel, DashboardPanel, and AdminDashboardPanel
 * switching
 */
public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private AdminLoginPanel adminLoginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private AdminDashboardPanel adminDashboardPanel;
    private User currentUser;

    public MainWindow() {
        setTitle("Smart Business Management Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Setup card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = new LoginPanel();
        adminLoginPanel = new AdminLoginPanel();
        registerPanel = new RegisterPanel();

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(adminLoginPanel, "AdminLogin");
        mainPanel.add(registerPanel, "Register");
        add(mainPanel);

        // Setup button actions
        loginPanel.getLoginButton().addActionListener(e -> handleLogin());
        loginPanel.getRegisterButton().addActionListener(e -> showRegisterPanel());
        loginPanel.getAdminLoginButton().addActionListener(e -> showAdminLoginPanel());

        adminLoginPanel.getLoginButton().addActionListener(e -> handleAdminLogin());
        adminLoginPanel.getBackButton().addActionListener(e -> showLoginPanel());

        registerPanel.getRegisterButton().addActionListener(e -> handleRegister());
        registerPanel.getBackButton().addActionListener(e -> showLoginPanel());

        setVisible(true);
    }

    private void handleLogin() {
        try {
            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();

            String usernameError = ValidationUtil.validateUsername(username);
            String passwordError = ValidationUtil.validateEmpty(password, "Password");
            if (usernameError != null || passwordError != null) {
                JOptionPane.showMessageDialog(this,
                        ValidationUtil.combineErrors(java.util.Arrays.asList(usernameError, passwordError)),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Attempt authentication
            currentUser = AuthService.authenticateUser(username, password);

            if (currentUser != null) {
                // Create dashboard panel if not already created
                if (dashboardPanel == null) {
                    dashboardPanel = new DashboardPanel(currentUser, this::handleLogout);
                    mainPanel.add(dashboardPanel, "Dashboard");
                } else {
                    // Update existing dashboard with new user
                    mainPanel.remove(dashboardPanel);
                    dashboardPanel = new DashboardPanel(currentUser, this::handleLogout);
                    mainPanel.add(dashboardPanel, "Dashboard");
                }
                cardLayout.show(mainPanel, "Dashboard");
                setTitle("Smart Business Management Suite - " + currentUser.getFullName());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                loginPanel.clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during login: " + e.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleAdminLogin() {
        try {
            String username = adminLoginPanel.getUsername();
            String password = adminLoginPanel.getPassword();

            // Authenticate via AuthService and require ADMIN role
            User authenticated = com.project.services.AuthService.authenticateUser(username, password);
            if (authenticated != null && "ADMIN".equalsIgnoreCase(authenticated.getRole())) {
                currentUser = authenticated;

                // Create admin dashboard
                if (adminDashboardPanel == null) {
                    adminDashboardPanel = new AdminDashboardPanel(currentUser, this::handleLogout);
                    mainPanel.add(adminDashboardPanel, "AdminDashboard");
                } else {
                    // Update existing admin dashboard
                    mainPanel.remove(adminDashboardPanel);
                    adminDashboardPanel = new AdminDashboardPanel(currentUser, this::handleLogout);
                    mainPanel.add(adminDashboardPanel, "AdminDashboard");
                }
                cardLayout.show(mainPanel, "AdminDashboard");
                setTitle("Smart Business Management Suite - Admin Dashboard");
            } else {
                // Generic error message to avoid revealing account details
                JOptionPane.showMessageDialog(this,
                        "Invalid admin credentials.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                adminLoginPanel.clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during admin login: " + e.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        currentUser = null;
        loginPanel.clearFields();
        adminLoginPanel.clearFields();
        cardLayout.show(mainPanel, "Login");
        setTitle("Smart Business Management Suite");
    }

    private void showAdminLoginPanel() {
        adminLoginPanel.clearFields();
        cardLayout.show(mainPanel, "AdminLogin");
    }

    private void showRegisterPanel() {
        registerPanel.clearFields();
        cardLayout.show(mainPanel, "Register");
    }

    private void showLoginPanel() {
        loginPanel.clearFields();
        cardLayout.show(mainPanel, "Login");
    }

    private void handleRegister() {
        try {
            String username = registerPanel.getUsername();
            String email = registerPanel.getEmail();
            String password = registerPanel.getPassword();
            String confirmPassword = registerPanel.getConfirmPassword();
            String fullName = registerPanel.getFullName();
            String phone = registerPanel.getPhone();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.List<String> validationErrors = ValidationUtil.validateRegistration(fullName, email, username,
                    password, phone);
            if (!validationErrors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ValidationUtil.combineErrors(validationErrors),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setRole("EMPLOYEE");

            User registeredUser = AuthService.registerUser(newUser, password);
            if (registeredUser != null) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful. You can now log in.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed. Please try again.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error during registration: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
