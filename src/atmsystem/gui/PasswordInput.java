package atmsystem.gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * A password field with an inline Show / Hide toggle button.
 * Drops in anywhere a single component is expected.
 */
public class PasswordInput extends JPanel {

    private final JPasswordField field;
    private final JButton toggle;
    private final char defaultEcho;
    private boolean visible = false;

    public PasswordInput() {
        super(new BorderLayout(6, 0));
        setOpaque(false);

        field = Theme.passwordField();
        defaultEcho = field.getEchoChar();

        toggle = Theme.smallToggleButton("Show");
        toggle.setPreferredSize(new Dimension(60, 28));
        toggle.setFocusable(false);
        toggle.addActionListener(e -> toggle());

        add(field, BorderLayout.CENTER);
        add(toggle, BorderLayout.EAST);
    }

    public char[] getPassword() {
        return field.getPassword();
    }

    public String getText() {
        return new String(field.getPassword());
    }

    public void clear() {
        field.setText("");
    }

    public JPasswordField getField() {
        return field;
    }

    private void toggle() {
        visible = !visible;
        field.setEchoChar(visible ? (char) 0 : defaultEcho);
        toggle.setText(visible ? "Hide" : "Show");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        field.setEnabled(enabled);
        toggle.setEnabled(enabled);
    }
}
