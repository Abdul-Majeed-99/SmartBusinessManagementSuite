package com.project.gui;

import com.project.models.User;
import com.project.gui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;

/**
 * DashboardPanel - Main application dashboard with logout
 */
public class DashboardPanel extends JPanel {
    private User currentUser;
    private JPanel contentPanel;
    private InventoryPanel inventoryPanel;
    private CategoryPanel categoryPanel;
    private SalesByCategoryPanel salesByCategoryPanel;
    private ExpensePanel expensePanel;
    private SalaryPanel salaryPanel;
    private ReportsPanel reportsPanel;
    private Runnable logoutCallback;

    public DashboardPanel(User user, Runnable logoutCallback) {
        this.currentUser = user;
        this.logoutCallback = logoutCallback;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(238, 242, 247));

        // Top panel with user info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        topPanel.setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel("Welcome, " + currentUser.getFullName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // User profile area in top-right
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        profilePanel.setBackground(new Color(255, 255, 255));
        JLabel userLabel = new JLabel(currentUser.getUsername() + " (" + currentUser.getRole() + ")");
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

        JLabel navigationLabel = new JLabel("Navigation");
        navigationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navigationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navigationLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton inventoryButton = createNavButton("Inventory");
        JButton categoriesButton = createNavButton("Categories");
        JButton salesButton = createNavButton("Sales by Category");
        JButton expensesButton = createNavButton("Expenses");
        JButton salaryButton = createNavButton("Salary");
        JButton reportsButton = createNavButton("Reports");

        sidebar.add(inventoryButton);
        sidebar.add(categoriesButton);
        sidebar.add(salesButton);
        sidebar.add(expensesButton);
        sidebar.add(salaryButton);
        sidebar.add(reportsButton);
        sidebar.add(Box.createVerticalGlue());

        // Logout button at bottom-left
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

        contentPanel = new JPanel(new CardLayout());
        boolean isAdmin = currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
        inventoryPanel = new InventoryPanel();
        categoryPanel = new CategoryPanel();
        salesByCategoryPanel = new SalesByCategoryPanel();
        expensePanel = new ExpensePanel(isAdmin);
        salaryPanel = new SalaryPanel(isAdmin);
        reportsPanel = new ReportsPanel();

        contentPanel.add(inventoryPanel, "Inventory");
        contentPanel.add(categoryPanel, "Categories");
        contentPanel.add(salesByCategoryPanel, "Sales by Category");
        contentPanel.add(expensePanel, "Expenses");
        contentPanel.add(salaryPanel, "Salary");
        contentPanel.add(reportsPanel, "Reports");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        inventoryButton.addActionListener(e -> switchCard("Inventory"));
        categoriesButton.addActionListener(e -> switchCard("Categories"));
        salesButton.addActionListener(e -> switchCard("Sales by Category"));
        expensesButton.addActionListener(e -> switchCard("Expenses"));
        salaryButton.addActionListener(e -> switchCard("Salary"));
        reportsButton.addActionListener(e -> switchCard("Reports"));

        // Default to Sales by Category for this workspace user's workflow
        switchCard("Sales by Category");
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(ThemeManager.BG_SECONDARY);
        button.setForeground(ThemeManager.TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setFont(ThemeManager.FONT_REGULAR);
        button.setBorder(ThemeManager.createTextFieldBorder());
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void switchCard(String name) {
        if ("Sales by Category".equals(name)) {
            salesByCategoryPanel.refreshCategories();
        } else if ("Expenses".equals(name)) {
            expensePanel.refreshCategories();
        }
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
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
