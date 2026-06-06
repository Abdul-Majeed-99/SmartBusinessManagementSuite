package com.project.models;

/**
 * SaleItem Model - Represents line items in sales
 */
public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    // Constructors
    public SaleItem() {}

    public SaleItem(int productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        updateTotal();
    }

    // Getters and Setters
    public int getSaleItemId() { return saleItemId; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; updateTotal(); }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; updateTotal(); }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    private void updateTotal() {
        this.totalPrice = quantity * unitPrice;
    }

    @Override
    public String toString() {
        return String.format("SaleItem{productId=%d, quantity=%d, total=%.2f}",
            productId, quantity, totalPrice);
    }
}
