package com.project.gui;

import com.project.database.DatabaseConnection;
import com.project.gui.theme.ThemeManager;
import com.project.models.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CategoryPanel - Displays available product categories
 */
public class CategoryPanel extends JPanel {
    private DefaultTableModel tableModel;

    public CategoryPanel() {
        setLayout(new BorderLayout(14, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ThemeManager.BG_PRIMARY);

        // Header panel with title and menu button
        JPanel headerPanel = ThemeManager.createStyledPanel();
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = ThemeManager.createTitleLabel("Category Manager");
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton menuButton = ThemeManager.createPrimaryButton("Add New Category");
        menuButton.setPreferredSize(new Dimension(160, ThemeManager.BUTTON_HEIGHT));
        menuButton.setToolTipText("Open category actions");

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addCategoryItem = new JMenuItem("Add New Category");
        addCategoryItem.addActionListener(e -> addNewCategory());
        popupMenu.add(addCategoryItem);

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> loadCategories());
        popupMenu.add(refreshItem);

        menuButton.addActionListener(e -> popupMenu.show(menuButton, 0, menuButton.getHeight()));

        JPanel buttonPanel = ThemeManager.createStyledPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.add(menuButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "Category", "Description", "Action" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        JTable categoryTable = new JTable(tableModel);
        ThemeManager.styleTable(categoryTable);
        categoryTable.getColumnModel().getColumn(2).setMinWidth(100);
        categoryTable.getColumnModel().getColumn(2).setMaxWidth(120);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        categoryTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        categoryTable.getColumnModel().getColumn(2)
                .setCellEditor(new ButtonEditor(new JCheckBox(), this::deleteCategory));

        // Right-click context menu
        JPopupMenu tablePopupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Selected");
        deleteItem.addActionListener(e -> {
            int selectedRow = categoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                deleteCategory(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a category to delete.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        tablePopupMenu.add(deleteItem);

        categoryTable.setComponentPopupMenu(tablePopupMenu);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        JLabel statusLabel = new JLabel("Right-click row or click Delete button to remove categories");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(100, 110, 120));
        footer.add(statusLabel);
        add(footer, BorderLayout.SOUTH);

        loadCategories();
    }

    private void addNewCategory() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Category", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        panel.add(new JLabel("Category Name:"), gbc);
        gbc.gridy++;
        JTextField categoryField = new JTextField();
        categoryField.setPreferredSize(new Dimension(250, 28));
        panel.add(categoryField, gbc);

        gbc.gridy++;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridy++;
        JTextArea descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 28));
        saveButton.addActionListener(e -> {
            String categoryName = categoryField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Category name cannot be empty.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
                DatabaseConnection.getInstance().executeUpdate(query, categoryName, description);

                JOptionPane.showMessageDialog(dialog,
                        "Category added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadCategories();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding category: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 28));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteCategory(int row) {
        if (row < 0 || row >= tableModel.getRowCount())
            return;

        String categoryName = (String) tableModel.getValueAt(row, 0);

        if (categoryName.equals("No categories found")) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete category: " + categoryName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = String.format("DELETE FROM categories WHERE category_name = '%s'",
                        categoryName.replace("'", "''"));
                DatabaseConnection.getInstance().executeUpdate(query);

                JOptionPane.showMessageDialog(this,
                        "Category deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadCategories();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting category: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT category_name, description FROM categories ORDER BY category_name";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("category_name"),
                        rs.getString("description"),
                        "Delete"
                });
            }
            if (tableModel.getRowCount() == 0) {
                tableModel
                        .addRow(new Object[] { "No categories found", "Use the database or import sample data.", "" });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load categories: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private interface RowAction {
        void perform(int row);
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
        private final RowAction action;

        public ButtonEditor(JCheckBox checkBox, RowAction action) {
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
            SwingUtilities.invokeLater(() -> action.perform(currentRow));
            return button.getText();
        }
    }
}
