package com.project.main;

import com.project.database.DatabaseConnection;
import com.project.gui.MainWindow;
import com.project.gui.SplashScreen;
import com.project.gui.theme.ThemeManager;
import javax.swing.*;

/**
 * ApplicationLauncher - Main entry point for Smart Business Management Suite
 * 
 * This is the ONLY main class needed. All modules are integrated through this
 * launcher.
 * Entry point: java -jar SmartBusinessManagementSuite.jar OR F5 in VS Code OR
 * Run in NetBeans
 * 
 * @author Smart Business Management Suite
 * @version 1.0
 */
public class ApplicationLauncher {

    public static void main(String[] args) {
        // Apply application theme first
        ThemeManager.applyTheme();

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }

        // Show splash screen
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection first
                System.out.println("\n========================================");
                System.out.println("  Smart Business Management Suite v1.0");
                System.out.println("========================================\n");
                System.out.println("Initializing application...");
                System.out.println("Testing database connection...");

                if (!DatabaseConnection.getInstance().testConnection()) {
                    JOptionPane.showMessageDialog(null,
                            "Database Connection Failed!\n\n" +
                                    "Please ensure:\n" +
                                    "1. MySQL is running (XAMPP)\n" +
                                    "2. Database 'smart_business_suite' exists\n" +
                                    "3. Run database_schema.sql first\n\n" +
                                    "Configuration: " + DatabaseConnection.getInstance().getConfiguration(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }

                System.out.println("✓ Database connection successful");

                // Show splash screen while loading
                SplashScreen splash = new SplashScreen();
                splash.setVisible(true);

                // Give time for splash to display
                Thread.sleep(2000);

                // Load and show main application window
                splash.dispose();
                System.out.println("✓ Loading main application...");

                MainWindow mainWindow = new MainWindow();
                System.out.println("✓ Application started successfully\n");

            } catch (Exception e) {
                System.err.println("✗ Application startup error: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Application Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
