package com.project.gui;

import com.project.database.DatabaseConnection;
import com.project.gui.theme.ThemeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * AdminSalaryPanel - Manage company salaries (Admin only)
 */
public class AdminSalaryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> employeeCombo;
    private JComboBox<String> typeCombo;
    private JSpinner dateSpinner;
    private JTextField amountField;
    private JTextArea notesArea;

    public AdminSalaryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(255, 255, 255));

        JLabel title = new JLabel("Salary Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createForm(), createTable());
        split.setResizeWeight(0.5);
        split.setDividerLocation(0.5);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);
        loadSalaryRecords();
    }

    private JPanel createForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add / Manage Salaries"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Employee"), gbc);
        gbc.gridx = 1;
        employeeCombo = new JComboBox<>();
        employeeCombo.setPreferredSize(new Dimension(240, 28));
        panel.add(employeeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Date"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setPreferredSize(new Dimension(240, 28));
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[] { "Salary", "Bonus", "Allowance", "Commission" });
        typeCombo.setPreferredSize(new Dimension(240, 28));
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Amount"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(20);
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 20);
        panel.add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addBtn = new JButton("Add Salary");
        addBtn.addActionListener(e -> addSalary());
        panel.add(addBtn, gbc);

        loadEmployees();
        return panel;
    }

    private JPanel createTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Salary Records"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        tableModel = new DefaultTableModel(
                new String[] { "Employee", "Date", "Type", "Amount", "Notes", "Action", "ID" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Action column is editable
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);

        // Set Action column width
        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor(this::handleTableAction));

        // Hide the ID column
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void handleTableAction(int row, String action) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;

        Object idObj = tableModel.getValueAt(row, 6);
        if (idObj == null)
            return;

        int salaryId = (Integer) idObj;
        String employee = (String) tableModel.getValueAt(row, 0);
        String date = (String) tableModel.getValueAt(row, 1);
        String type = (String) tableModel.getValueAt(row, 2);
        String amount = (String) tableModel.getValueAt(row, 3);

        if ("Delete".equals(action)) {
            deleteSalaryRecord(salaryId, employee, date, type, amount);
        }
    }

    private void deleteSalaryRecord(int salaryId, String employee, String date, String type, String amount) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this salary record?\n" +
                        "Employee: " + employee + "\nDate: " + date + "\nType: " + type + "\nAmount: " + amount,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection.getInstance().executeUpdate(
                        "DELETE FROM salary_records WHERE salary_id = ?", salaryId);
                JOptionPane.showMessageDialog(this, "Salary record deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSalaryRecords();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Unable to delete salary record: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadEmployees() {
        employeeCombo.removeAllItems();
        try {
            ResultSet rs = DatabaseConnection.getInstance()
                    .executeQuery("SELECT user_id, full_name FROM users ORDER BY full_name");
            while (rs.next()) {
                employeeCombo.addItem(rs.getInt("user_id") + ": " + rs.getString("full_name"));
            }
            if (employeeCombo.getItemCount() == 0) {
                employeeCombo.addItem("No employees");
            }
        } catch (SQLException e) {
            employeeCombo.addItem("(DB error) - load failed");
            System.err.println("Unable to load employees for salary panel: " + e.getMessage());
        }
    }

    private void addSalary() {
        String emp = (String) employeeCombo.getSelectedItem();
        if (emp == null || emp.startsWith("No ") || emp.startsWith("(DB")) {
            JOptionPane.showMessageDialog(this, "No employee selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String type = (String) typeCombo.getSelectedItem();
        String amount = amountField.getText().trim();
        String notes = notesArea.getText().trim();
        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Extract user_id from the combo box selection
            String empString = emp.split(":")[0].trim();
            int userId = Integer.parseInt(empString);

            DatabaseConnection.getInstance().executeUpdate(
                    "INSERT INTO salary_records (employee_id, salary_date, type, amount, notes) VALUES (?, ?, ?, ?, ?)",
                    userId, date, type, Double.parseDouble(amount), notes);

            JOptionPane.showMessageDialog(this, "Salary record added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            amountField.setText("");
            notesArea.setText("");
            loadSalaryRecords();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to save salary record: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSalaryRecords() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT sr.salary_id, u.full_name, sr.salary_date, sr.type, sr.amount, sr.notes " +
                    "FROM salary_records sr " +
                    "JOIN users u ON sr.employee_id = u.user_id " +
                    "ORDER BY sr.salary_date DESC";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("full_name"),
                        rs.getString("salary_date"),
                        rs.getString("type"),
                        rs.getString("amount"),
                        rs.getString("notes"),
                        "Delete",
                        rs.getInt("salary_id")
                });
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[] { "No salary records", "", "", "", "", "", null });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load salary records: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class for rendering action buttons
    private static class ActionButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ActionButtonRenderer() {
            setOpaque(true);
            setFont(ThemeManager.FONT_REGULAR);
            setBackground(new Color(255, 230, 230));
            setForeground(ThemeManager.TEXT_PRIMARY);
            setFocusPainted(false);
            setContentAreaFilled(true);
            setBorderPainted(true);
            setBorder(BorderFactory.createLineBorder(new Color(220, 80, 80), 1));
            setPreferredSize(new Dimension(110, ThemeManager.BUTTON_HEIGHT));
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            if (isSelected) {
                setBackground(ThemeManager.DANGER_COLOR.darker());
            } else {
                setBackground(ThemeManager.DANGER_COLOR);
            }
            return this;
        }
    }

    // Inner class for editing action buttons
    private class ActionButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;
        private final java.util.function.BiConsumer<Integer, String> action;

        public ActionButtonEditor(java.util.function.BiConsumer<Integer, String> action) {
            super(new JCheckBox());
            this.action = action;
            button = new JButton();
            button.setOpaque(true);
            button.setFont(ThemeManager.FONT_REGULAR);
            button.setBackground(new Color(255, 230, 230));
            button.setForeground(ThemeManager.TEXT_PRIMARY);
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createLineBorder(new Color(220, 80, 80), 1));
            button.setPreferredSize(new Dimension(110, ThemeManager.BUTTON_HEIGHT));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                int row, int column) {
            currentRow = row;
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            SwingUtilities.invokeLater(() -> action.accept(currentRow, button.getText()));
            return button.getText();
        }
    }
}
