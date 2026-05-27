package system.gui;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.models.Account;
import system.models.Bank;
import system.models.Customer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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

public class AccountCreationGUI extends JFrame {

    public static final double MIN_INITIAL_BALANCE = 100.0;

    private final Customer customer;
    private final JTextField accountNumberField;
    private final JComboBox<String> accountTypeBox;
    private final JTextField initialBalanceField;
    private final PasswordInput passwordInput;
    private final PasswordInput confirmPasswordInput;

    public AccountCreationGUI(Customer customer) {
        this.customer = customer;

        setTitle("ET Banking system - Account Creation");
        setSize(680, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("ACCOUNT CREATION"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        accountNumberField = Theme.textField();
        accountNumberField.setEditable(false);
        accountNumberField.setBackground(new java.awt.Color(220, 220, 220));

        accountTypeBox = new JComboBox<>(new String[] { "Savings", "Current", "Fixed Deposit" });
        accountTypeBox.setFont(Theme.FONT_FIELD);

        initialBalanceField = Theme.textField();
        passwordInput = new PasswordInput();
        confirmPasswordInput = new PasswordInput();

        addRow(form, gc, 0, "Account Number:", accountNumberField);
        addRow(form, gc, 1, "Account Type:", accountTypeBox);
        addRow(form, gc, 2, "Initial Balance (" + Theme.CURRENCY + ", min 100):", initialBalanceField);
        addRow(form, gc, 3, "Password:", passwordInput);
        addRow(form, gc, 4, "Confirm Password:", confirmPasswordInput);

        JButton create = Theme.primaryButton("CREATE ACCOUNT");
        JButton back = Theme.secondaryButton("BACK");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);
        buttons.add(create);
        buttons.add(back);

        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        create.addActionListener(e -> createAccount());
        back.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });

        autoGenerateAccountNumber();
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String label,
            java.awt.Component field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel(label), gc);
        gc.gridx = 1;
        gc.gridy = row;
        gc.weightx = 1.0;
        field.setPreferredSize(new Dimension(320, 32));
        form.add(field, gc);
    }

    private void autoGenerateAccountNumber() {
        try {
            accountNumberField.setText(DatabaseHandler.generateAccountNumber());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not generate account number.\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAccount() {
        String accountNumber = accountNumberField.getText();
        String type = (String) accountTypeBox.getSelectedItem();
        String balanceStr = initialBalanceField.getText().trim();
        String password = passwordInput.getText();
        String confirm = confirmPasswordInput.getText();

        if (balanceStr.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Initial balance must be a valid number.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (balance < MIN_INITIAL_BALANCE) {
            JOptionPane.showMessageDialog(this,
                    "Initial balance must be at least " + Theme.money(MIN_INITIAL_BALANCE) + ".",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Account account = Bank.createAccount(type, accountNumber, customer.getCustomerId(), balance, password);
            DatabaseHandler.saveAccount(account);
            FileHandler.logEvent("ACCOUNT_CREATED " + accountNumber + " (" + type + ")");
            JOptionPane.showMessageDialog(this,
                    "Account Successfully Created!\n\nAccount Number: " + accountNumber
                            + "\nType: " + type
                            + "\nInitial Balance: " + Theme.money(balance),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginGUI().setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
