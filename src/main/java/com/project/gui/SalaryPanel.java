package com.project.gui;

import com.project.database.DatabaseConnection;
import com.project.gui.theme.ThemeManager;
import com.project.validation.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SalaryPanel - Salary and income management
 */
public class SalaryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JSpinner dateSpinner;
    private JComboBox<String> typeCombo;
    private JTextArea notesArea;
    private boolean isAdmin;
    private JLabel totalSalaryLabel;
    private JLabel totalBonusLabel;
    private JLabel totalAllowanceLabel;
    private JLabel grandTotalLabel;

    public SalaryPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(255, 255, 255));

        String title = isAdmin ? "Salary & Income Management" : "Salary Summary";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        if (isAdmin) {
            // Admin: Show form panel + table panel with split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
            splitPane.setDividerLocation(420);
            splitPane.setResizeWeight(0.4);
            add(splitPane, BorderLayout.CENTER);
        } else {
            // Non-admin: Show only table panel with summary (2 panels)
            add(createTablePanel(), BorderLayout.CENTER);
        }
        loadSalaryRecords();
    }

    private JPanel createFormPanel() {
        if (!isAdmin) {
            JPanel readOnlyPanel = new JPanel(new BorderLayout());
            readOnlyPanel.setBackground(new Color(250, 250, 250));
            readOnlyPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Salary Records (Read Only)"),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)));
            JLabel readOnlyLabel = new JLabel("Salary record entry is restricted to administrators.",
                    SwingConstants.CENTER);
            readOnlyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            readOnlyLabel.setForeground(new Color(85, 100, 130));
            readOnlyPanel.add(readOnlyLabel, BorderLayout.CENTER);
            return readOnlyPanel;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add Salary / Income"),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Date"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setPreferredSize(new Dimension(180, 28));
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[] { "Salary", "Bonus", "Allowance", "Commission" });
        typeCombo.setPreferredSize(new Dimension(140, 28));
        JButton addTypeBtn = new JButton("+");
        addTypeBtn.setPreferredSize(new Dimension(36, 28));
        addTypeBtn.addActionListener(e -> {
            String newType = JOptionPane.showInputDialog(this, "Enter new income type:");
            if (newType != null) {
                newType = newType.trim();
                if (!newType.isEmpty()) {
                    ((javax.swing.DefaultComboBoxModel<String>) typeCombo.getModel()).addElement(newType);
                    typeCombo.setSelectedItem(newType);
                }
            }
        });
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        typePanel.setBackground(panel.getBackground());
        typePanel.add(typeCombo);
        typePanel.add(addTypeBtn);
        panel.add(typePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Amount"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(18);
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(4, 18);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Record");
        addButton.addActionListener(e -> addSalaryRecord());
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255));

        // Left side - Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Salary Records"),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        String[] columns = isAdmin
                ? new String[] { "Date", "Type", "Amount", "Notes", "Action", "ID" }
                : new String[] { "Date", "Type", "Amount", "Notes", "ID" };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isAdmin && column == 4;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        if (isAdmin) {
            table.getColumnModel().getColumn(4).setMaxWidth(80);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);

            table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(4)
                    .setCellEditor(new ButtonEditor(new JCheckBox(), this::deleteSalaryRecord));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPopupMenu popup = new JPopupMenu();
            JMenuItem deleteItem = new JMenuItem("Delete Selected");
            deleteItem.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    deleteSalaryRecord(selectedRow);
                }
            });
            popup.add(deleteItem);
            table.setComponentPopupMenu(popup);
        } else {
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Right panel showing salary summary
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(new Color(245, 248, 252));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Salary Summary"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        summaryPanel.setPreferredSize(new Dimension(220, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        summaryPanel.add(new JLabel("Total Salary:"), gbc);
        gbc.gridy++;
        totalSalaryLabel = new JLabel("₨ 0.00");
        totalSalaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalSalaryLabel.setForeground(new Color(40, 120, 180));
        summaryPanel.add(totalSalaryLabel, gbc);

        gbc.gridy++;
        summaryPanel.add(new JLabel("Total Bonus:"), gbc);
        gbc.gridy++;
        totalBonusLabel = new JLabel("₨ 0.00");
        totalBonusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalBonusLabel.setForeground(new Color(40, 120, 180));
        summaryPanel.add(totalBonusLabel, gbc);

        gbc.gridy++;
        summaryPanel.add(new JLabel("Total Allowance:"), gbc);
        gbc.gridy++;
        totalAllowanceLabel = new JLabel("₨ 0.00");
        totalAllowanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalAllowanceLabel.setForeground(new Color(40, 120, 180));
        summaryPanel.add(totalAllowanceLabel, gbc);

        gbc.gridy++;
        summaryPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        summaryPanel.add(new JLabel("Grand Total:"), gbc);
        gbc.gridy++;
        grandTotalLabel = new JLabel("₨ 0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        grandTotalLabel.setForeground(new Color(34, 139, 34));
        summaryPanel.add(grandTotalLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1.0;
        JButton refreshSummaryBtn = ThemeManager.createStyledButton("Refresh");
        refreshSummaryBtn.setPreferredSize(new Dimension(120, ThemeManager.BUTTON_HEIGHT));
        refreshSummaryBtn.addActionListener(e -> updateSalarySummary());
        summaryPanel.add(refreshSummaryBtn, gbc);

        mainPanel.add(summaryPanel, BorderLayout.EAST);

        return mainPanel;
    }

    private void loadSalaryRecords() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT salary_id, salary_date, type, amount, notes FROM salary_records ORDER BY salary_date DESC";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                if (isAdmin) {
                    tableModel.addRow(new Object[] {
                            rs.getString("salary_date"),
                            rs.getString("type"),
                            rs.getString("amount"),
                            rs.getString("notes"),
                            "Delete",
                            rs.getInt("salary_id")
                    });
                } else {
                    tableModel.addRow(new Object[] {
                            rs.getString("salary_date"),
                            rs.getString("type"),
                            rs.getString("amount"),
                            rs.getString("notes"),
                            rs.getInt("salary_id")
                    });
                }
            }
            if (tableModel.getRowCount() == 0) {
                if (isAdmin) {
                    tableModel.addRow(new Object[] { "No salary records found", "", "", "", "", null });
                } else {
                    tableModel.addRow(new Object[] { "No salary records found", "", "", "", null });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load salary records: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        updateSalarySummary();
    }

    private void deleteSalaryRecord(int row) {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "Only administrators can delete salary records.",
                    "Permission Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (row < 0 || row >= tableModel.getRowCount())
            return;

        Object idValue = tableModel.getValueAt(row, 5);
        if (idValue == null) {
            return;
        }

        int salaryId = (Integer) idValue;
        String date = (String) tableModel.getValueAt(row, 0);
        String type = (String) tableModel.getValueAt(row, 1);
        String amount = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this salary record?\n" +
                        "Date: " + date + ", Type: " + type + ", Amount: " + amount,
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

    private void updateSalarySummary() {
        double totalSalary = 0;
        double totalBonus = 0;
        double totalAllowance = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String type = (String) tableModel.getValueAt(i, 1);
            String amountStr = (String) tableModel.getValueAt(i, 2);

            try {
                double amount = Double.parseDouble(amountStr);
                if ("Salary".equals(type)) {
                    totalSalary += amount;
                } else if ("Bonus".equals(type)) {
                    totalBonus += amount;
                } else if ("Allowance".equals(type)) {
                    totalAllowance += amount;
                }
            } catch (NumberFormatException e) {
                // Skip invalid amounts
            }
        }

        double grandTotal = totalSalary + totalBonus + totalAllowance;
        totalSalaryLabel.setText(String.format("₨ %.2f", totalSalary));
        totalBonusLabel.setText(String.format("₨ %.2f", totalBonus));
        totalAllowanceLabel.setText(String.format("₨ %.2f", totalAllowance));
        grandTotalLabel.setText(String.format("₨ %.2f", grandTotal));
    }

    private void addSalaryRecord() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "You do not have permission to add salary records. Please contact an administrator.",
                    "Permission Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String type = (String) typeCombo.getSelectedItem();
        String amount = amountField.getText().trim();
        String notes = notesArea.getText().trim();

        java.util.List<String> errors = new java.util.ArrayList<>();
        errors.add(ValidationUtil.validateDate(date, "Date"));
        errors.add(ValidationUtil.validateCategoryName(type));
        errors.add(ValidationUtil.validateAmount(amount, "Amount"));
        errors.add(ValidationUtil.validateNotes(notes));
        errors.removeIf(item -> item == null);

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    ValidationUtil.combineErrors(errors),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseConnection.getInstance().executeUpdate(
                    "INSERT INTO salary_records (salary_date, type, amount, notes) VALUES (?, ?, ?, ?)",
                    java.sql.Date.valueOf(date), type, Double.parseDouble(amount), notes);
            JOptionPane.showMessageDialog(this, "Salary record added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dateSpinner.setValue(new Date());
            amountField.setText("");
            notesArea.setText("");
            loadSalaryRecords();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to save salary record: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
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

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;
        private final java.util.function.IntConsumer action;

        public ButtonEditor(JCheckBox checkBox, java.util.function.IntConsumer action) {
            super(checkBox);
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
            SwingUtilities.invokeLater(() -> action.accept(currentRow));
            return button.getText();
        }
    }
}
