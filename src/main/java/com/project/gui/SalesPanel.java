package com.project.gui;

import javax.swing.*;
import java.awt.*;

/**
 * SalesPanel - Sales transaction management
 */
public class SalesPanel extends JPanel {
    
    public SalesPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Sales Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);
        
        JButton newSaleButton = new JButton("New Sale");
        JButton viewSalesButton = new JButton("View Sales");
        JButton generateInvoiceButton = new JButton("Generate Invoice");
        JButton processPaymentButton = new JButton("Process Payment");
        
        add(newSaleButton);
        add(viewSalesButton);
        add(generateInvoiceButton);
        add(processPaymentButton);
    }
}
