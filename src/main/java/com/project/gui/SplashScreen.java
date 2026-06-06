package com.project.gui;

import javax.swing.*;
import java.awt.*;

/**
 * SplashScreen - Application startup splash screen
 */
public class SplashScreen extends JFrame {
    
    public SplashScreen() {
        setTitle("Smart Business Management Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        // Create content panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw background gradient
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color[] colors = {new Color(41, 128, 185), new Color(52, 152, 219)};
                GradientPaint gradient = new GradientPaint(0, 0, colors[0], 0, getHeight(), colors[1]);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw title
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 32));
                String title = "Smart Business Management Suite";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                g2d.drawString(title, x, 100);
                
                // Draw loading message
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                String loading = "Loading...";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(loading)) / 2;
                g2d.drawString(loading, x, 150);
            }
        };
        
        panel.setPreferredSize(new Dimension(600, 300));
        add(panel);
        pack();
    }
}
