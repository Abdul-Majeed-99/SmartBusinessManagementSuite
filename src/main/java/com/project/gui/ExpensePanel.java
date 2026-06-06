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
 * ExpensePanel - Expense tracking with category selection
 */
public class ExpensePanel extends JPanel {
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JSpinner dateSpinner;
    private JTextArea notesArea;
    private boolean isAdmin;

    public ExpensePanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ThemeManager.BG_PRIMARY);

        String title = isAdmin ? "Expense Management" : "Company Expenses";
        JLabel titleLabel = ThemeManager.createTitleLabel(title);
        add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormPanel(), createTablePanel());
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);
        add(splitPane, BorderLayout.CENTER);
        loadExpenses();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BG_TERTIARY);
        panel.setBorder(ThemeManager.createTitledBorder("Add Expense"));
        // make form panel wider for easier data entry
        panel.setPreferredSize(new Dimension(520, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.PADDING_LG, ThemeManager.PADDING_LG, ThemeManager.PADDING_LG,
                ThemeManager.PADDING_LG);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(ThemeManager.createStyledLabel("Date"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setPreferredSize(new Dimension(220, ThemeManager.TEXT_FIELD_HEIGHT));
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Category"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        categoryCombo = ThemeManager.createStyledComboBox();
        panel.add(categoryCombo, gbc);
        loadCategories();

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton addCategoryBtn = ThemeManager.createStyledButton("Add Category");
        addCategoryBtn.setPreferredSize(new Dimension(130, ThemeManager.BUTTON_HEIGHT_SM));
        addCategoryBtn.setToolTipText("Add a new expense category");
        addCategoryBtn.addActionListener(e -> {
            String newCat = JOptionPane.showInputDialog(this, "Enter new category name:");
            if (newCat != null) {
                newCat = newCat.trim();
                if (!newCat.isEmpty()) {
                    try {
                        String query = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
                        DatabaseConnection.getInstance().executeUpdate(query, newCat, "Expense category");
                        loadCategories();
                        categoryCombo.setSelectedItem(newCat);
                        JOptionPane.showMessageDialog(this,
                                "Category added successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        String message = ex.getMessage();
                        if (message != null && message.toLowerCase().contains("duplicate")) {
                            JOptionPane.showMessageDialog(this,
                                    "Category already exists.",
                                    "Duplicate Category",
                                    JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Unable to add category: " + ex.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        panel.add(addCategoryBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(ThemeManager.createStyledLabel("Amount"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        amountField = ThemeManager.createStyledTextField();
        amountField.setPreferredSize(new Dimension(340, ThemeManager.TEXT_FIELD_HEIGHT));
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(ThemeManager.createStyledLabel("Notes"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        notesArea = ThemeManager.createStyledTextArea(4, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(ThemeManager.createTextFieldBorder());
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(360, 140));
        panel.add(notesScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = ThemeManager.createPrimaryButton("Add Expense");
        addButton.setPreferredSize(new Dimension(200, ThemeManager.BUTTON_HEIGHT));
        addButton.addActionListener(e -> addExpense());
        // Allow both admins and employees to add expenses
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = ThemeManager.createStyledPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(ThemeManager.createTitledBorder("Expenses"));

        tableModel = new DefaultTableModel(new String[] { "Date", "Category", "Amount", "Notes", "Action", "ID" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JTable table = new JTable(tableModel);
        ThemeManager.styleTable(table);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(4).setMinWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(110);
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setPreferredWidth(0);

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), this::deleteExpense));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Selected");
        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                deleteExpense(selectedRow);
            }
        });
        popup.add(deleteItem);
        table.setComponentPopupMenu(popup);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT expense_id, expense_date, category, amount, notes FROM expenses ORDER BY expense_date DESC";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("expense_date"),
                        rs.getString("category"),
                        rs.getString("amount"),
                        rs.getString("notes"),
                        "Delete",
                        rs.getInt("expense_id")
                });
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[] { "No expenses found", "", "", "", "", null });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load expenses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense(int row) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;

        Object idValue = tableModel.getValueAt(row, 5);
        if (idValue == null) {
            return;
        }

        int expenseId = (Integer) idValue;
        String date = (String) tableModel.getValueAt(row, 0);
        String category = (String) tableModel.getValueAt(row, 1);
        String amount = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?\n" +
                        "Date: " + date + ", Category: " + category + ", Amount: " + amount,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection.getInstance().executeUpdate(
                        "DELETE FROM expenses WHERE expense_id = ?", expenseId);
                JOptionPane.showMessageDialog(this, "Expense deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadExpenses();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Unable to delete expense: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addExpense() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String category = (String) categoryCombo.getSelectedItem();
        String amount = amountField.getText().trim();
        String notes = notesArea.getText().trim();

        java.util.List<String> errors = new java.util.ArrayList<>();
        errors.add(ValidationUtil.validateDate(date, "Date"));
        errors.add(ValidationUtil.validateCategoryName(category));
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
                    "INSERT INTO expenses (expense_date, category, amount, notes) VALUES (?, ?, ?, ?)",
                    java.sql.Date.valueOf(date), category, Double.parseDouble(amount), notes);
            JOptionPane.showMessageDialog(this, "Expense added successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dateSpinner.setValue(new Date());
            amountField.setText("");
            notesArea.setText("");
            loadExpenses();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to save expense: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCategories() {
        loadCategories();
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT category_name FROM categories ORDER BY category_name");
            while (rs.next()) {
                categoryCombo.addItem(rs.getString("category_name"));
            }
            if (categoryCombo.getItemCount() == 0) {
                addDefaultCategoryOptions();
            }
        } catch (SQLException e) {
            addDefaultCategoryOptions();
        }
    }

    private void addDefaultCategoryOptions() {
        categoryCombo.addItem("Office Supplies");
        categoryCombo.addItem("Utilities");
        categoryCombo.addItem("Travel");
        categoryCombo.addItem("Maintenance");
        categoryCombo.addItem("Other");
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
