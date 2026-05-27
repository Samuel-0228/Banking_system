package system.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class Theme {

    public static final Color BG_TEAL = new Color(30, 96, 122);
    public static final Color BG_DARK_TEAL = new Color(22, 75, 96);
    public static final Color BG_PANEL = new Color(40, 116, 145);
    public static final Color BTN_DARK = new Color(20, 20, 20);
    public static final Color BTN_HOVER = new Color(55, 55, 55);
    public static final Color BTN_LIGHT = new Color(245, 245, 245);
    public static final Color BTN_LIGHT_HOV = new Color(220, 220, 220);
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_DARK = new Color(30, 30, 30);
    public static final Color FIELD_BG = Color.WHITE;
    public static final Color ACCENT_YELLOW = new Color(255, 213, 79);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_FIELD = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.BOLD, 11);

    public static final String CURRENCY = "ETB";

    private Theme() {
    }

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_WHITE);
        return l;
    }

    public static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_WHITE);
        return l;
    }

    public static JTextField textField() {
        JTextField f = new JTextField();
        f.setFont(FONT_FIELD);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT_DARK);
        f.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_FIELD);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT_DARK);
        f.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return f;
    }

    public static JButton primaryButton(String text) {
        return makeButton(text, BTN_DARK, TEXT_WHITE, BTN_HOVER, 18, 8, FONT_BUTTON);
    }

    public static JButton secondaryButton(String text) {
        return makeButton(text, BTN_LIGHT, TEXT_DARK, BTN_LIGHT_HOV, 18, 8, FONT_BUTTON);
    }

    public static JButton smallToggleButton(String text) {
        return makeButton(text, BTN_LIGHT, TEXT_DARK, BTN_LIGHT_HOV, 10, 4, FONT_SMALL);
    }

    /**
     * Custom-painted button so background is honored regardless of the platform
     * look-and-feel (macOS Aqua, in particular, ignores setBackground on JButton).
     */
    private static JButton makeButton(String text, final Color bg, Color fg,
            final Color hover, int padX, int padY, Font font) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paintColor = bg;
                if (getModel().isPressed()) {
                    paintColor = bg.darker();
                } else if (getModel().isRollover()) {
                    paintColor = hover;
                }
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(font);
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(padY, padX, padY, padX));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static Border panelBorder() {
        return BorderFactory.createEmptyBorder(20, 30, 20, 30);
    }

    /** Format a money amount with the ETB currency suffix. */
    public static String money(double amount) {
        return String.format("%,.2f %s", amount, CURRENCY);
    }
}
