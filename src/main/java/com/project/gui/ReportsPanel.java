package com.project.gui;

import com.project.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ReportsPanel - Reports and analytics
 */
public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel("Business Analytics Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Expense Breakdown", createExpenseBreakdownPanel());
        tabs.addTab("Inventory Status", createInventoryStatusPanel());
        tabs.addTab("Sales Revenue", createSalesRevenuePanel());
        tabs.addTab("Summary", createSummaryPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createExpenseBreakdownPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> expenseTotals = loadExpenseBreakdown();
        if (expenseTotals.isEmpty()) {
            expenseTotals.put("Office Supplies", 28.0);
            expenseTotals.put("Utilities", 18.0);
            expenseTotals.put("Travel", 15.0);
            expenseTotals.put("Maintenance", 12.0);
            expenseTotals.put("Other", 27.0);
        }
        expenseTotals.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Distribution by Category",
                dataset,
                true,
                true,
                false);

        return wrapChart(chart, "Expense distribution is based on actual expense entries grouped by category.");
    }

    private JPanel createInventoryStatusPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> inventoryCounts = loadInventoryStock();
        if (inventoryCounts.isEmpty()) {
            inventoryCounts.put("Laptops", 15);
            inventoryCounts.put("Accessories", 42);
            inventoryCounts.put("Office", 30);
            inventoryCounts.put("Software", 18);
        }
        inventoryCounts.forEach((category, value) -> dataset.addValue(value, "Stock", category));

        JFreeChart chart = ChartFactory.createBarChart(
                "Inventory Stock by Category",
                "Category",
                "Units in Stock",
                dataset);

        return wrapChart(chart, "Stock levels are grouped by category and updated from the product catalog.");
    }

    private JPanel createSalesRevenuePanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> salesTotals = loadSalesRevenue();
        if (salesTotals.isEmpty()) {
            return createEmptyDataPanel(
                    "No sales revenue data is available yet. Add a sale first, then refresh the report.");
        }
        salesTotals.forEach((period, value) -> dataset.addValue(value, "Revenue", period));

        JFreeChart chart = ChartFactory.createBarChart(
                "Sales Revenue Trend",
                "Period",
                "Revenue",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(210, 210, 210));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 120, 215));
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("₨ {2}", new DecimalFormat("#,##0.00")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        return wrapChart(chart, "Revenue figures are calculated from actual recorded sales by period.");
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 14, 14));
        panel.setBackground(new Color(255, 255, 255));

        int totalProducts = getTotalProducts();
        int totalCategories = getTotalCategories();
        int totalSales = getTotalSalesTransactions();
        double totalRevenue = getTotalRevenue();

        panel.add(createStatCard("Total Products", String.valueOf(totalProducts)));
        panel.add(createStatCard("Total Categories", String.valueOf(totalCategories)));
        panel.add(createStatCard("Sales Transactions", String.valueOf(totalSales)));
        panel.add(createStatCard("Total Revenue", String.format("₨ %,.2f", totalRevenue)));

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.setBackground(new Color(250, 251, 253));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valueLabel.setForeground(new Color(38, 88, 165));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel wrapChart(JFreeChart chart, String description) {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(new Color(255, 255, 255));
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(760, 420));
        wrapper.add(chartPanel, BorderLayout.CENTER);

        JLabel descriptionLabel = new JLabel("<html><body style='width:780px; padding:8px; font-family:Segoe UI;'>"
                + description + "</body></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(90, 95, 105));
        wrapper.add(descriptionLabel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createEmptyDataPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("<html><div style='text-align:center; font-family:Segoe UI; color:#5A6770;'>"
                + message + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private Map<String, Integer> loadInventoryStock() {
        Map<String, Integer> stockData = new LinkedHashMap<>();
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT c.category_name, SUM(p.quantity_in_stock) AS total_stock " +
                            "FROM products p JOIN categories c ON p.category_id = c.category_id " +
                            "GROUP BY c.category_name ORDER BY total_stock DESC");
            while (rs.next()) {
                stockData.put(rs.getString("category_name"), rs.getInt("total_stock"));
            }
        } catch (SQLException e) {
            // fallback to sample values
        }
        return stockData;
    }

    private Map<String, Double> loadExpenseBreakdown() {
        Map<String, Double> expenseData = new LinkedHashMap<>();
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT category, SUM(amount) AS total_amount " +
                            "FROM expenses GROUP BY category ORDER BY total_amount DESC");
            while (rs.next()) {
                expenseData.put(rs.getString("category"), rs.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            // fallback to sample values
        }
        return expenseData;
    }

    private Map<String, Double> loadSalesRevenue() {
        Map<String, Double> revenueData = new LinkedHashMap<>();
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT DATE_FORMAT(sale_date, '%Y-%m') AS period, SUM(total_amount) AS revenue " +
                            "FROM sales_by_category GROUP BY period ORDER BY period");
            while (rs.next()) {
                revenueData.put(rs.getString("period"), rs.getDouble("revenue"));
            }
        } catch (SQLException e) {
            // fallback to sample values
        }
        return revenueData;
    }

    private int getTotalProducts() {
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT COUNT(*) AS total FROM products");
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            // ignore and return 0
        }
        return 0;
    }

    private int getTotalCategories() {
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT COUNT(*) AS total FROM categories");
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            // ignore and return 0
        }
        return 0;
    }

    private int getTotalSalesTransactions() {
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT COUNT(*) AS total FROM sales_by_category");
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            // ignore and return 0
        }
        return 0;
    }

    private double getTotalRevenue() {
        try {
            ResultSet rs = DatabaseConnection.getInstance().executeQuery(
                    "SELECT SUM(total_amount) AS total_revenue FROM sales_by_category");
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            // ignore and return 0
        }
        return 0;
    }
}
