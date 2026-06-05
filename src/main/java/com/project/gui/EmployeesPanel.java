package com.project.gui;

import com.project.database.DatabaseConnection;
import com.project.gui.theme.ThemeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * EmployeesPanel - Display and manage employee/user data (Admin only)
 */
public class EmployeesPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;

    public EmployeesPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(245, 248, 252));

        JLabel titleLabel = new JLabel("User & Employee Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Username", "Full Name", "Email", "Phone", "Role", "Status", "Last Login" }, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);

        // Make table columns wider for readability
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(140);

        loadEmployeeData();
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = ThemeManager.createStyledButton("Refresh");
        refreshButton.setForeground(Color.BLACK);
        refreshButton.addActionListener(e -> loadEmployeeData());
        footer.add(refreshButton);
        tablePanel.add(footer, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT user_id, username, full_name, email, phone, role, status, last_login FROM users ORDER BY user_id";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toString() : "Never"
                });
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[] { "-", "No users found", "", "", "", "", "", "" });
            }
        } catch (SQLException e) {
            tableModel.addRow(new Object[] { "-", "Unable to load user data", "", "", "", "", "", "" });
            System.err.println("Error loading employee data: " + e.getMessage());
        }
    }

    public void refreshData() {
        loadEmployeeData();
    }
}
