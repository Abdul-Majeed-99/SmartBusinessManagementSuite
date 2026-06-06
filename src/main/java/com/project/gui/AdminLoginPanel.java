package com.project.gui;

import com.project.gui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;

/**
 * AdminLoginPanel - Admin authentication UI
 * Default credentials: username: admin, password: admin
 */
public class AdminLoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    public AdminLoginPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.BG_SECONDARY);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.BG_PRIMARY);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subtitleLabel = new JLabel("Administrator Login");
        subtitleLabel.setFont(ThemeManager.FONT_REGULAR);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        card.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(ThemeManager.createStyledLabel("Username"), gbc);

        gbc.gridx = 1;
        usernameField = ThemeManager.createStyledTextField();
        usernameField.setPreferredSize(new Dimension(360, 34));
        usernameField.setText("");
        card.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(ThemeManager.createStyledLabel("Password"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(ThemeManager.FONT_REGULAR);
        passwordField.setPreferredSize(new Dimension(360, 34));
        passwordField.setBorder(ThemeManager.createTextFieldBorder());
        passwordField.setText("");
        card.add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(ThemeManager.BG_PRIMARY);
        loginButton = ThemeManager.createPrimaryButton("Admin Login");
        loginButton.setForeground(Color.BLACK);
        backButton = ThemeManager.createStyledButton("Back to User Login");
        backButton.setForeground(Color.BLACK);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        card.add(buttonPanel, gbc);

        gbc.gridy = 5;
        JLabel helpLabel = new JLabel("Enter administrator credentials to continue.");
        helpLabel.setFont(ThemeManager.FONT_SMALL);
        helpLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        card.add(helpLabel, gbc);

        add(card);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
