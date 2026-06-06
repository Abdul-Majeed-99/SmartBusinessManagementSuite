package com.project.gui;

import javax.swing.*;
import java.awt.*;

/**
 * RegisterPanel - User registration UI
 */
public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 252));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230)),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        card.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(24);
        usernameField.setPreferredSize(new Dimension(320, 34));
        card.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(24);
        emailField.setPreferredSize(new Dimension(320, 34));
        card.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(24);
        passwordField.setPreferredSize(new Dimension(320, 34));
        card.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        card.add(new JLabel("Confirm Password"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(24);
        confirmPasswordField.setPreferredSize(new Dimension(320, 34));
        card.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        card.add(new JLabel("Full Name"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(24);
        fullNameField.setPreferredSize(new Dimension(320, 34));
        card.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        card.add(new JLabel("Phone"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(24);
        phoneField.setPreferredSize(new Dimension(320, 34));
        card.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(Color.WHITE);
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        card.add(buttonPanel, gbc);

        add(card);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public void clearFields() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
    }
}
