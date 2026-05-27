package system.gui;

import system.exceptions.InvalidAmountException;
import system.operations.Deposit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class DepositGUI extends JDialog {

    public DepositGUI(DashboardGUI parent) {
        super(parent, "Deposit Money", true);
        setSize(420, 240);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("DEPOSIT"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField amountField = Theme.textField();
        amountField.setPreferredSize(new Dimension(220, 30));

        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Amount (" + Theme.CURRENCY + "):"), gc);
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        form.add(amountField, gc);

        JButton confirm = Theme.primaryButton("DEPOSIT");
        JButton cancel = Theme.secondaryButton("CANCEL");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);
        buttons.add(confirm);
        buttons.add(cancel);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        confirm.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                new Deposit(parent.getAccount(), amount).execute();
                parent.reloadAccount();
                JOptionPane.showMessageDialog(this,
                        "Deposit successful. New balance: "
                                + Theme.money(parent.getAccount().getBalance()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (InvalidAmountException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dispose());
    }
}
