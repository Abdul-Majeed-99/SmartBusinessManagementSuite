package com.project.gui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * ThemeManager - Centralized styling and theming for the entire application
 * Provides consistent look and feel across all UI components
 */
public class ThemeManager {
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Light Blue
    public static final Color SUCCESS_COLOR = new Color(34, 139, 34); // Green
    public static final Color DANGER_COLOR = new Color(220, 80, 80); // Red
    public static final Color WARNING_COLOR = new Color(255, 165, 0); // Orange
    public static final Color INFO_COLOR = new Color(30, 144, 255); // Dodger Blue

    public static final Color BG_PRIMARY = new Color(255, 255, 255); // White
    public static final Color BG_SECONDARY = new Color(245, 248, 252); // Very Light Blue
    public static final Color BG_TERTIARY = new Color(250, 250, 250); // Light Gray

    public static final Color TEXT_PRIMARY = new Color(0, 0, 0); // Black - Maximum Visibility
    public static final Color TEXT_SECONDARY = new Color(40, 40, 40); // Very Dark Gray
    public static final Color TEXT_HINT = new Color(80, 80, 80); // Dark Gray

    public static final Color BORDER_COLOR = new Color(225, 230, 240); // Very Light Blue
    public static final Color DIVIDER_COLOR = new Color(210, 220, 235); // Light Border

    // Font Configuration
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 12);

    // Spacing Constants
    public static final int PADDING_XS = 4;
    public static final int PADDING_SM = 8;
    public static final int PADDING_MD = 12;
    public static final int PADDING_LG = 16;
    public static final int PADDING_XL = 24;

    public static final int BORDER_RADIUS = 8;

    // Component Sizes
    public static final int BUTTON_HEIGHT = 32;
    public static final int BUTTON_HEIGHT_SM = 28;
    public static final int TEXT_FIELD_HEIGHT = 28;
    public static final int COMBO_BOX_HEIGHT = 28;

    /**
     * Apply theme to entire application
     */
    public static void applyTheme() {
        try {
            // Set modern look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Override defaults
            UIManager.put("Button.font", FONT_BUTTON);
            UIManager.put("Label.font", FONT_REGULAR);
            UIManager.put("TextField.font", FONT_REGULAR);
            UIManager.put("TextArea.font", FONT_REGULAR);
            UIManager.put("ComboBox.font", FONT_REGULAR);
            UIManager.put("Table.font", FONT_REGULAR);
            UIManager.put("TitledBorder.font", FONT_REGULAR);

            // Colors
            UIManager.put("Button.background", SECONDARY_COLOR);
            UIManager.put("Button.foreground", TEXT_PRIMARY); // Use dark button text by default for visibility
            UIManager.put("OptionPane.buttonForeground", TEXT_PRIMARY);
            UIManager.put("Label.foreground", TEXT_PRIMARY); // Black text
            UIManager.put("TextField.background", BG_PRIMARY);
            UIManager.put("TextField.foreground", TEXT_PRIMARY); // Black text
            UIManager.put("TextField.caretForeground", PRIMARY_COLOR);

            UIManager.put("Panel.background", BG_PRIMARY);
            UIManager.put("ComboBox.background", BG_PRIMARY);
            UIManager.put("Table.background", BG_PRIMARY);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.selectionBackground", SECONDARY_COLOR);
            UIManager.put("Table.selectionForeground", BG_PRIMARY);

            UIManager.put("TabbedPane.background", BG_PRIMARY);
            UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
            UIManager.put("TabbedPane.selected", SECONDARY_COLOR);
            UIManager.put("OptionPane.messageFont", FONT_REGULAR);
            UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
            UIManager.put("OptionPane.background", BG_PRIMARY);
            UIManager.put("Panel.background", BG_PRIMARY);
            UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);

        } catch (Exception e) {
            System.err.println("Failed to set theme: " + e.getMessage());
        }
    }

    /**
     * Create a styled button
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        button.setFont(FONT_BUTTON);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(getContrastTextColor(SECONDARY_COLOR));
        button.setPreferredSize(new Dimension(140, BUTTON_HEIGHT));
        button.setFocusPainted(false);
        // We paint the background ourselves; disable LAF background painting
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR.darker(), 1));
        button.setOpaque(false);
        // Keep rollover handling off so LAF doesn't change visuals
        try {
            button.setRolloverEnabled(false);
        } catch (Throwable t) {
            // ignore
        }
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect that explicitly sets colors (prevents LAF interference)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(getContrastTextColor(PRIMARY_COLOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
                button.setForeground(getContrastTextColor(SECONDARY_COLOR));
            }
        });

        return button;
    }

    /**
     * Create a styled primary button (larger)
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = createStyledButton(text);
        button.setPreferredSize(new Dimension(220, BUTTON_HEIGHT));
        button.setFont(FONT_SUBHEADING);
        button.setForeground(getContrastTextColor(button.getBackground()));
        return button;
    }

    /**
     * Create a danger button (red)
     */
    public static JButton createDangerButton(String text) {
        JButton button = createStyledButton(text);
        button.setBackground(DANGER_COLOR);
        button.setForeground(getContrastTextColor(button.getBackground()));
        button.setPreferredSize(new Dimension(160, BUTTON_HEIGHT));
        button.setFont(FONT_SUBHEADING);
        button.setBorder(BorderFactory.createLineBorder(DANGER_COLOR.darker(), 1));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 60, 60));
                button.setForeground(getContrastTextColor(button.getBackground()));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DANGER_COLOR);
                button.setForeground(getContrastTextColor(button.getBackground()));
            }
        });
        return button;
    }

    /**
     * Create a success button (green)
     */
    public static JButton createSuccessButton(String text) {
        JButton button = createStyledButton(text);
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(getContrastTextColor(button.getBackground()));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 120, 25));
                button.setForeground(getContrastTextColor(button.getBackground()));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SUCCESS_COLOR);
                button.setForeground(getContrastTextColor(button.getBackground()));
            }
        });
        return button;
    }

    private static Color getContrastTextColor(Color background) {
        int r = background.getRed();
        int g = background.getGreen();
        int b = background.getBlue();
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b);
        return luminance > 160 ? TEXT_PRIMARY : Color.WHITE;
    }

    /**
     * Create a styled text field
     */
    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_REGULAR);
        field.setBackground(BG_PRIMARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_COLOR);
        field.setBorder(createTextFieldBorder());
        field.setPreferredSize(new Dimension(320, TEXT_FIELD_HEIGHT));
        return field;
    }

    /**
     * Create a styled text area
     */
    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(FONT_REGULAR);
        area.setBackground(BG_PRIMARY);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(PRIMARY_COLOR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(createTextFieldBorder());
        // make default areas a bit more comfortable for notes
        if (cols < 40) {
            area.setColumns(Math.max(cols, 40));
        }
        if (rows < 4) {
            area.setRows(Math.max(rows, 4));
        }
        return area;
    }

    /**
     * Create a styled combo box
     */
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(FONT_REGULAR);
        combo.setBackground(BG_PRIMARY);
        combo.setForeground(TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(260, COMBO_BOX_HEIGHT));
        return combo;
    }

    /**
     * Create a styled label
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Create a styled title label
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    /**
     * Create a styled heading label
     */
    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Create a panel with standard styling
     */
    public static JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PRIMARY);
        return panel;
    }

    /**
     * Create a panel with form styling
     */
    public static JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_TERTIARY);
        panel.setBorder(createPanelBorder());
        return panel;
    }

    /**
     * Create a panel with section styling
     */
    public static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SECONDARY);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        title,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        FONT_SUBHEADING,
                        TEXT_PRIMARY),
                BorderFactory.createEmptyBorder(PADDING_MD, PADDING_MD, PADDING_MD, PADDING_MD));
        panel.setBorder(border);
        return panel;
    }

    /**
     * Create a border for text fields
     */
    public static Border createTextFieldBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, false),
                new EmptyBorder(PADDING_SM, PADDING_SM, PADDING_SM, PADDING_SM));
    }

    /**
     * Create a border for panels
     */
    public static Border createPanelBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, false),
                new EmptyBorder(PADDING_MD, PADDING_MD, PADDING_MD, PADDING_MD));
    }

    /**
     * Create a border with title
     */
    public static Border createTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(PADDING_MD, PADDING_MD, PADDING_MD, PADDING_MD));
    }

    /**
     * Style a JTable
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setBackground(BG_PRIMARY);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(BG_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(24);

        // Header styling
        table.getTableHeader().setBackground(BG_SECONDARY);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(FONT_SUBHEADING);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    /**
     * Create a styled separator
     */
    public static JSeparator createStyledSeparator() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(BORDER_COLOR);
        return sep;
    }

    /**
     * Create empty space panel for padding
     */
    public static JPanel createSpacerPanel(int height) {
        JPanel spacer = new JPanel();
        spacer.setBackground(BG_PRIMARY);
        spacer.setPreferredSize(new Dimension(0, height));
        return spacer;
    }
}
