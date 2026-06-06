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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SalesByCategoryPanel - Sales transaction management by product category
 */
public class SalesByCategoryPanel extends JPanel {
    private DefaultTableModel saleItemsModel;
    private DefaultTableModel salesHistoryModel;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> productCombo;
    private JSpinner quantitySpinner;
    private JTextField unitPriceField;
    private JTextField discountField;
    private JTextArea notesArea;
    private JLabel totalLabel;
    private JTable saleItemsTable;
    private double saleTotal = 0;
    private List<Map<String, Object>> currentSaleItems = new ArrayList<>();

    public SalesByCategoryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ThemeManager.BG_PRIMARY);

        JLabel titleLabel = ThemeManager.createTitleLabel("Sales by Category");
        add(titleLabel, BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createFormAndItemsPanel(),
                createHistoryPanel());
        mainSplit.setResizeWeight(0.5);
        mainSplit.setDividerLocation(0.5);
        add(mainSplit, BorderLayout.CENTER);

        loadSalesHistory();
    }

    private JPanel createFormAndItemsPanel() {
        JPanel mainPanel = ThemeManager.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createFormPanel(), createItemsPanel());
        split.setResizeWeight(0.5);
        split.setDividerLocation(0.5);
        mainPanel.add(split, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BG_TERTIARY);
        panel.setBorder(ThemeManager.createTitledBorder("Add Sale Item"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.PADDING_SM, ThemeManager.PADDING_SM, ThemeManager.PADDING_SM,
                ThemeManager.PADDING_SM);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        panel.add(ThemeManager.createStyledLabel("Category"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        categoryCombo = ThemeManager.createStyledComboBox();
        categoryCombo.setPreferredSize(new Dimension(360, ThemeManager.COMBO_BOX_HEIGHT));
        panel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(ThemeManager.createStyledLabel("Product"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        productCombo = ThemeManager.createStyledComboBox();
        productCombo.setPreferredSize(new Dimension(360, ThemeManager.COMBO_BOX_HEIGHT));
        panel.add(productCombo, gbc);

        // Add action listener AFTER productCombo is initialized
        categoryCombo.addActionListener(e -> loadProductsByCategory());
        loadCategories();

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(ThemeManager.createStyledLabel("Quantity"), gbc);
        gbc.gridx = 1;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        quantitySpinner.setPreferredSize(new Dimension(120, ThemeManager.TEXT_FIELD_HEIGHT));
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Unit Price"), gbc);
        gbc.gridx = 1;
        unitPriceField = ThemeManager.createStyledTextField();
        unitPriceField.setPreferredSize(new Dimension(240, ThemeManager.TEXT_FIELD_HEIGHT));
        panel.add(unitPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(ThemeManager.createStyledLabel("Discount (%)"), gbc);
        gbc.gridx = 1;
        discountField = ThemeManager.createStyledTextField();
        discountField.setPreferredSize(new Dimension(160, ThemeManager.TEXT_FIELD_HEIGHT));
        discountField.setText("0");
        panel.add(discountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        panel.add(ThemeManager.createStyledLabel("Notes"), gbc);
        gbc.gridy++;
        notesArea = ThemeManager.createStyledTextArea(4, 40);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(420, 120));
        panel.add(notesScroll, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addItemBtn = ThemeManager.createPrimaryButton("Add Item to Sale");
        addItemBtn.setPreferredSize(new Dimension(260, ThemeManager.BUTTON_HEIGHT));
        addItemBtn.addActionListener(e -> addSaleItem());
        panel.add(addItemBtn, gbc);

        return panel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = ThemeManager.createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(ThemeManager.createTitledBorder("Sale Items"));

        saleItemsModel = new DefaultTableModel(
                new String[] { "Category", "Product", "Qty", "Unit Price", "Discount %", "Total", "Action", "ID" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        saleItemsTable = new JTable(saleItemsModel);
        ThemeManager.styleTable(saleItemsTable);
        saleItemsTable.getColumnModel().getColumn(6).setMinWidth(90);
        saleItemsTable.getColumnModel().getColumn(6).setMaxWidth(100);
        saleItemsTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        saleItemsTable.getColumnModel().getColumn(7).setMinWidth(0);
        saleItemsTable.getColumnModel().getColumn(7).setMaxWidth(0);
        saleItemsTable.getColumnModel().getColumn(7).setPreferredWidth(0);

        saleItemsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        saleItemsTable.getColumnModel().getColumn(6).setCellEditor(
                new ButtonEditor(new JCheckBox(), this::removeSaleItem));

        panel.add(new JScrollPane(saleItemsTable), BorderLayout.CENTER);

        JPanel bottomPanel = ThemeManager.createStyledPanel();
        bottomPanel.setLayout(new BorderLayout());

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        totalsPanel.setBackground(ThemeManager.BG_SECONDARY);
        totalsPanel.add(ThemeManager.createHeadingLabel("Sale Total:"));
        totalLabel = new JLabel("₨ 0.00");
        totalLabel.setFont(ThemeManager.FONT_SUBHEADING);
        totalLabel.setForeground(ThemeManager.SUCCESS_COLOR);
        totalsPanel.add(totalLabel);

        JButton saveSaleBtn = ThemeManager.createSuccessButton("Save Sale");
        saveSaleBtn.addActionListener(e -> saveSale());
        totalsPanel.add(saveSaleBtn);

        JButton clearBtn = ThemeManager.createStyledButton("Clear");
        clearBtn.addActionListener(e -> clearSale());
        totalsPanel.add(clearBtn);

        panel.add(totalsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = ThemeManager.createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(ThemeManager.createTitledBorder("Sales History"));

        salesHistoryModel = new DefaultTableModel(
                new String[] { "Date", "Category", "Product", "Quantity", "Unit Price", "Total", "Notes" }, 0);

        JTable historyTable = new JTable(salesHistoryModel);
        ThemeManager.styleTable(historyTable);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel refreshPanel = ThemeManager.createStyledPanel();
        refreshPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = ThemeManager.createStyledButton("Refresh");
        refreshBtn.addActionListener(e -> loadSalesHistory());
        refreshPanel.add(refreshBtn);
        panel.add(refreshPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void refreshCategories() {
        loadCategories();
        if (categoryCombo.getSelectedItem() != null) {
            loadProductsByCategory();
        }
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT category_name FROM categories ORDER BY category_name");
            while (rs.next()) {
                categoryCombo.addItem(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            categoryCombo.addItem("Error loading categories");
        }
    }

    private void loadProductsByCategory() {
        productCombo.removeAllItems();
        String category = (String) categoryCombo.getSelectedItem();
        if (category == null || category.isEmpty())
            return;

        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT product_name, unit_price FROM products WHERE category_id = " +
                            "(SELECT category_id FROM categories WHERE category_name = ?) ORDER BY product_name",
                    category);
            while (rs.next()) {
                String productName = rs.getString("product_name");
                double price = rs.getDouble("unit_price");
                productCombo.addItem(productName + " (₨" + String.format("%.2f", price) + ")");
            }
            if (productCombo.getItemCount() == 0) {
                productCombo.addItem("No products in this category");
            }
        } catch (SQLException e) {
            productCombo.addItem("Error loading products");
        }
    }

    private void addSaleItem() {
        String category = (String) categoryCombo.getSelectedItem();
        String product = (String) productCombo.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();
        String unitPriceStr = unitPriceField.getText().trim();
        String discountStr = discountField.getText().trim();

        if (category == null || category.isEmpty() || product == null || product.contains("No products")) {
            JOptionPane.showMessageDialog(this, "Please select a valid category and product.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (unitPriceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter unit price.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double unitPrice = Double.parseDouble(unitPriceStr);
            double discount = discountStr.isEmpty() ? 0 : Double.parseDouble(discountStr);

            if (discount < 0 || discount > 100) {
                JOptionPane.showMessageDialog(this, "Discount must be between 0 and 100.", "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double subtotal = unitPrice * quantity;
            double discountAmount = (subtotal * discount) / 100;
            double total = subtotal - discountAmount;

            String productName = product.split("\\(")[0].trim();
            saleItemsModel.addRow(new Object[] { category, productName, quantity, unitPrice, discount, total, "Remove",
                    System.nanoTime() });

            Map<String, Object> item = new HashMap<>();
            item.put("category", category);
            item.put("product", productName);
            item.put("quantity", quantity);
            item.put("unitPrice", unitPrice);
            item.put("discount", discount);
            item.put("total", total);
            item.put("notes", notesArea.getText());
            currentSaleItems.add(item);

            updateSaleTotal();
            clearForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price or discount value.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSaleItem(int row) {
        if (row >= 0 && row < saleItemsModel.getRowCount()) {
            saleItemsModel.removeRow(row);
            if (row < currentSaleItems.size()) {
                currentSaleItems.remove(row);
            }
            updateSaleTotal();
        }
    }

    private void updateSaleTotal() {
        saleTotal = 0;
        for (int i = 0; i < saleItemsModel.getRowCount(); i++) {
            saleTotal += (double) saleItemsModel.getValueAt(i, 5);
        }
        totalLabel.setText("₨ " + String.format("%.2f", saleTotal));
    }

    private void saveSale() {
        if (saleItemsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to save the sale.", "Empty Sale",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String saleDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            for (int i = 0; i < saleItemsModel.getRowCount(); i++) {
                String category = (String) saleItemsModel.getValueAt(i, 0);
                String product = (String) saleItemsModel.getValueAt(i, 1);
                int quantity = (Integer) saleItemsModel.getValueAt(i, 2);
                double unitPrice = (double) saleItemsModel.getValueAt(i, 3);
                double discount = (double) saleItemsModel.getValueAt(i, 4);
                double total = (double) saleItemsModel.getValueAt(i, 5);

                String query = "INSERT INTO sales_by_category (sale_date, category, product_name, quantity, unit_price, discount, total_amount, notes) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                DatabaseConnection.getInstance().executeUpdate(query,
                        java.sql.Date.valueOf(saleDate), category, product, quantity, unitPrice, discount, total, "");
            }

            JOptionPane.showMessageDialog(this, "Sale saved successfully. Total: ₨ " + String.format("%.2f", saleTotal),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearSale();
            loadSalesHistory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving sale: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSale() {
        saleItemsModel.setRowCount(0);
        currentSaleItems.clear();
        updateSaleTotal();
        clearForm();
    }

    private void clearForm() {
        unitPriceField.setText("");
        discountField.setText("0");
        notesArea.setText("");
        quantitySpinner.setValue(1);
    }

    private void loadSalesHistory() {
        salesHistoryModel.setRowCount(0);
        try {
            String query = "SELECT sale_date, category, product_name, quantity, unit_price, total_amount, notes " +
                    "FROM sales_by_category ORDER BY sale_date DESC LIMIT 100";
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(query);
            while (rs.next()) {
                salesHistoryModel.addRow(new Object[] {
                        rs.getString("sale_date"),
                        rs.getString("category"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total_amount"),
                        rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading sales history: " + e.getMessage());
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
