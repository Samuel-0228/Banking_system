package system.gui;

import system.exceptions.AccountNotFoundException;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;
import system.operations.Transfer;

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

public class TransferGUI extends JDialog {

    public TransferGUI(DashboardGUI parent) {
        super(parent, "Transfer Money", true);
        setSize(480, 320);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("TRANSFER"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField receiverField = Theme.textField();
        receiverField.setPreferredSize(new Dimension(260, 32));
        JTextField amountField = Theme.textField();
        amountField.setPreferredSize(new Dimension(260, 32));

        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Receiver Account:"), gc);
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        form.add(receiverField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Amount (" + Theme.CURRENCY + "):"), gc);
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1.0;
        form.add(amountField, gc);

        JButton confirm = Theme.primaryButton("TRANSFER");
        JButton cancel = Theme.secondaryButton("CANCEL");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);
        buttons.add(confirm);
        buttons.add(cancel);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        confirm.addActionListener(e -> {
            String receiver = receiverField.getText().trim();
            if (receiver.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the receiver account number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!promptAndVerifyPassword(parent))
                return;

            try {
                new Transfer(parent.getAccount(), receiver, amount).execute();
                parent.reloadAccount();
                JOptionPane.showMessageDialog(this,
                        "Transfer successful.\nNew balance: "
                                + Theme.money(parent.getAccount().getBalance()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (InvalidAmountException | InsufficientFundsException | AccountNotFoundException ex) {
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
                "Enter your password to confirm this transfer",
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
