package com.project.models;

import java.time.LocalDateTime;

/**
 * Product Model - Represents inventory products
 */
public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private int categoryId;
    private String description;
    private double unitPrice;
    private int quantityInStock;
    private int reorderLevel;
    private String supplierName;
    private LocalDateTime createdAt;

    // Constructors
    public Product() {}

    public Product(String productCode, String productName, int categoryId, double unitPrice, int quantityInStock) {
        this.productCode = productCode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = 10;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isLowStock() {
        return quantityInStock <= reorderLevel;
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, code='%s', name='%s', stock=%d}",
            productId, productCode, productName, quantityInStock);
    }
}
