package atmsystem.gui;

import atmsystem.exceptions.InsufficientFundsException;
import atmsystem.exceptions.InvalidAmountException;
import atmsystem.operations.Withdraw;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class WithdrawGUI extends JDialog {

    public WithdrawGUI(DashboardGUI parent) {
        super(parent, "Withdraw Money", true);
        setSize(440, 260);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("WITHDRAW"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField amountField = Theme.textField();
        amountField.setPreferredSize(new Dimension(240, 32));

        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Amount (" + Theme.CURRENCY + "):"), gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        form.add(amountField, gc);

        JButton confirm = Theme.primaryButton("WITHDRAW");
        JButton cancel = Theme.secondaryButton("CANCEL");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);
        buttons.add(confirm);
        buttons.add(cancel);

        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        confirm.addActionListener(e -> {
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!promptAndVerifyPassword(parent)) return;

            try {
                new Withdraw(parent.getAccount(), amount).execute();
                parent.reloadAccount();
                JOptionPane.showMessageDialog(this,
                        "Withdrawal successful. New balance: "
                                + Theme.money(parent.getAccount().getBalance()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (InvalidAmountException | InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dispose());
    }

    /** Prompt for password and verify against the logged-in account. */
    private boolean promptAndVerifyPassword(DashboardGUI parent) {
        JPasswordField pwd = Theme.passwordField();
        pwd.setPreferredSize(new Dimension(220, 28));
        int result = JOptionPane.showConfirmDialog(this, pwd,
                "Enter your password to confirm this withdrawal",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }
        String entered = new String(pwd.getPassword());
        if (!parent.getAccount().verifyPassword(entered)) {
            JOptionPane.showMessageDialog(this, "Incorrect password.",
                    "Authentication failed", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
