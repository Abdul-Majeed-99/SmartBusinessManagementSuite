package com.project.models;

import java.time.LocalDateTime;

/**
 * Sale Model - Represents sales transactions
 */
public class Sale {
    private int saleId;
    private LocalDateTime saleDate;
    private int userId;
    private String customerName;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private String notes;

    // Constructors
    public Sale() {
        this.status = "COMPLETED";
        this.paymentStatus = "PENDING";
        this.paymentMethod = "CASH";
    }

    public Sale(int userId, String customerName, double subtotal, double taxAmount, double discountAmount) {
        this();
        this.userId = userId;
        this.customerName = customerName;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = subtotal + taxAmount - discountAmount;
    }

    // Getters and Setters
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; updateTotal(); }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; updateTotal(); }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; updateTotal(); }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    private void updateTotal() {
        this.totalAmount = subtotal + taxAmount - discountAmount;
    }

    @Override
    public String toString() {
        return String.format("Sale{id=%d, customer='%s', total=%.2f, status='%s'}",
            saleId, customerName, totalAmount, status);
    }
}
