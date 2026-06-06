package com.project.gui;

import com.project.gui.theme.ThemeManager;
import com.project.models.User;
import javax.swing.*;
import java.awt.*;

/**
 * AdminDashboardPanel - Admin dashboard with employee management
 */
public class AdminDashboardPanel extends JPanel {
    private User currentUser;
    private JPanel contentPanel;
    private EmployeesPanel employeesPanel;
    private Runnable logoutCallback;

    public AdminDashboardPanel(User user, Runnable logoutCallback) {
        this.currentUser = user;
        this.logoutCallback = logoutCallback;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(238, 242, 247));

        // Top panel with user info and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        topPanel.setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel("Admin Panel - Welcome, " + currentUser.getFullName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // User profile area in top-right
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        profilePanel.setBackground(new Color(255, 255, 255));
        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(85, 100, 130));
        profilePanel.add(userLabel);
        topPanel.add(profilePanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 8));
        sidebar.setBackground(new Color(245, 248, 252));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel navigationLabel = new JLabel("Admin Navigation");
        navigationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navigationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navigationLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton employeesButton = createNavButton("Employees");
        JButton reportsButton = createNavButton("Reports");
        JButton salariesButton = createNavButton("Salaries");
        JButton settingsButton = createNavButton("Settings");

        sidebar.add(employeesButton);
        sidebar.add(reportsButton);
        sidebar.add(salariesButton);
        sidebar.add(settingsButton);
        sidebar.add(Box.createVerticalGlue());

        // Logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setBackground(new Color(220, 80, 80));
        logoutButton.setForeground(ThemeManager.TEXT_PRIMARY);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(true);
        logoutButton.addActionListener(e -> performLogout());
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(logoutButton);

        // Content panel
        contentPanel = new JPanel(new CardLayout());
        employeesPanel = new EmployeesPanel();
        contentPanel.add(employeesPanel, "Employees");
        contentPanel.add(new ReportsPanel(), "Reports");
        contentPanel.add(new AdminSalaryPanel(), "Salaries");
        contentPanel.add(createSettingsPanel(), "Settings");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        employeesButton.addActionListener(e -> switchCard("Employees"));
        reportsButton.addActionListener(e -> switchCard("Reports"));
        salariesButton.addActionListener(e -> switchCard("Salaries"));
        settingsButton.addActionListener(e -> switchCard("Settings"));
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel("Admin Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        contentPanel.add(new JLabel("System Configuration"), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Database: smart_business_suite"), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("Admin User: " + currentUser.getUsername()), gbc);
        gbc.gridy++;
        contentPanel.add(new JLabel("All features enabled"), gbc);

        panel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        return panel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setPreferredSize(new Dimension(200, 48));
        button.setBackground(ThemeManager.BG_PRIMARY);
        button.setForeground(ThemeManager.TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setFont(ThemeManager.FONT_SUBHEADING);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void switchCard(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        if ("Employees".equals(name)) {
            employeesPanel.refreshData();
        }
    }

    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (logoutCallback != null) {
                logoutCallback.run();
            }
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
