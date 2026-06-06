package com.project.models;

import java.time.LocalDateTime;

/**
 * Invoice Model - Represents formal invoices
 */
public class Invoice {
    private int invoiceId;
    private int saleId;
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime invoiceDate;
    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;
    private double taxPercent;
    private double discountPercent;
    private String status;
    private String notes;

    // Constructors
    public Invoice() {
        this.status = "DRAFT";
    }

    public Invoice(int saleId, String invoiceNumber, String customerName, double totalAmount) {
        this();
        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.remainingAmount = totalAmount;
    }

    // Getters and Setters
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; updateRemaining(); }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; updateRemaining(); }

    public double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }

    public double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(double taxPercent) { this.taxPercent = taxPercent; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    private void updateRemaining() {
        this.remainingAmount = totalAmount - paidAmount;
    }

    public boolean isPaid() {
        return remainingAmount <= 0;
    }

    @Override
    public String toString() {
        return String.format("Invoice{number='%s', customer='%s', total=%.2f, remaining=%.2f, status='%s'}",
            invoiceNumber, customerName, totalAmount, remainingAmount, status);
    }
}
