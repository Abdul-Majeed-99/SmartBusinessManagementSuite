package com.project.gui;

import com.project.database.DatabaseConnection;
import com.project.gui.theme.ThemeManager;
import com.project.validation.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryPanel - Product inventory management
 */
public class InventoryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryCombo;

    public InventoryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ThemeManager.BG_PRIMARY);

        JLabel titleLabel = ThemeManager.createTitleLabel("Inventory Management");
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Products", createProductListPanel());
        tabs.addTab("Add Product", createAddProductPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createProductListPanel() {
        JPanel panel = ThemeManager.createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(new String[] { "Code", "Name", "Category", "Price", "Stock" }, 0);
        loadProducts();

        JTable table = new JTable(tableModel);
        ThemeManager.styleTable(table);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(ThemeManager.BG_PRIMARY);
        JButton refreshButton = ThemeManager.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadProducts());
        footer.add(refreshButton);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(ThemeManager.createStyledLabel("Product Code"), gbc);
        gbc.gridx = 1;
        JTextField codeField = ThemeManager.createStyledTextField();
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Product Name"), gbc);
        gbc.gridx = 1;
        JTextField nameField = ThemeManager.createStyledTextField();
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Category"), gbc);
        gbc.gridx = 1;
        categoryCombo = ThemeManager.createStyledComboBox();
        loadCategories();
        panel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Unit Price"), gbc);
        gbc.gridx = 1;
        JTextField priceField = ThemeManager.createStyledTextField();
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Stock Quantity"), gbc);
        gbc.gridx = 1;
        JTextField stockField = ThemeManager.createStyledTextField();
        panel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = ThemeManager.createSuccessButton("Add Product");
        addButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryCombo.getSelectedItem() == null ? "" : categoryCombo.getSelectedItem().toString();
            String price = priceField.getText().trim();
            String stock = stockField.getText().trim();

            List<String> errors = new ArrayList<>();
            errors.add(ValidationUtil.validateProductCode(code));
            errors.add(ValidationUtil.validateProductName(name));
            errors.add(ValidationUtil.validateProductCategory(category));
            errors.add(ValidationUtil.validateAmount(price, "Unit Price"));
            errors.add(ValidationUtil.validateInteger(stock, "Stock Quantity"));
            errors.removeIf(item -> item == null);

            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        ValidationUtil.combineErrors(errors),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                // Ensure category exists and get category_id
                Integer categoryId = null;
                ResultSet crs = DatabaseConnection.getInstance().executeQuery(
                        "SELECT category_id FROM categories WHERE category_name = ?", category);
                if (crs.next()) {
                    categoryId = crs.getInt("category_id");
                } else {
                    // Insert new category
                    ResultSet keys = DatabaseConnection.getInstance().executeUpdateReturnKeys(
                            "INSERT INTO categories (category_name, description) VALUES (?, ?)", category, "");
                    if (keys.next()) {
                        categoryId = keys.getInt(1);
                    }
                }

                // Insert product
                double unitPrice = Double.parseDouble(price);
                int quantity = Integer.parseInt(stock);
                DatabaseConnection.getInstance().executeUpdate(
                        "INSERT INTO products (product_code, product_name, category_id, unit_price, quantity_in_stock) VALUES (?, ?, ?, ?, ?)",
                        code, name, categoryId, unitPrice, quantity);

                // Refresh list from DB so other panels can see the new product
                loadProducts();
                JOptionPane.showMessageDialog(this, "Product added and saved to database.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                codeField.setText("");
                nameField.setText("");
                priceField.setText("");
                stockField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Unable to save product: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton, gbc);

        return panel;
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

    private void loadProducts() {
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT p.product_code, p.product_name, c.category_name, p.unit_price, p.quantity_in_stock " +
                            "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id ORDER BY p.product_name");
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("product_code"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getBigDecimal("unit_price").toPlainString(),
                        rs.getInt("quantity_in_stock")
                });
            }
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[] { "No products found", "", "", "", "" });
            }
        } catch (SQLException e) {
            tableModel.addRow(new Object[] { "Unable to load products", "", "", "", "" });
        }
    }
}
