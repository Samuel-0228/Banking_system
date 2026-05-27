package system.gui;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InvalidLoginException;
import system.models.Account;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class LoginGUI extends JFrame {

    private final JTextField accountField;
    private final PasswordInput passwordInput;

    public LoginGUI() {
        setTitle("ET Banking system - Login");
        setSize(760, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setLayout(new BorderLayout());
        header.setBorder(Theme.panelBorder());

        JLabel bankIcon = new JLabel("🏦", SwingConstants.CENTER);
        bankIcon.setFont(bankIcon.getFont().deriveFont(60f));
        bankIcon.setForeground(Theme.TEXT_WHITE);

        JLabel title = new JLabel("WELCOME TO ET BANKING SYSTEM", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BorderLayout());
        titleBlock.add(bankIcon, BorderLayout.NORTH);
        titleBlock.add(title, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));
        searchPanel.setOpaque(false);
        JLabel searchLabel = Theme.formLabel("Search Account:");
        searchLabel.setForeground(Theme.TEXT_WHITE);
        JTextField searchField = Theme.textField();
        searchField.setPreferredSize(new Dimension(200, 32));
        JButton searchButton = Theme.secondaryButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 32));
        searchButton.addActionListener(e -> {
            String accNo = searchField.getText().trim();
            if (accNo.isEmpty()) {
                JOptionPane.showMessageDialog(LoginGUI.this, "Please enter an account number.", "Search", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (DatabaseHandler.accountExists(accNo)) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Account " + accNo + " exists.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Account " + accNo + " does not exist.", "Search Result", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        header.add(titleBlock, BorderLayout.CENTER);
        header.add(searchPanel, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_TEAL);
        form.setBorder(Theme.panelBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Account No:"), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        accountField = Theme.textField();
        accountField.setPreferredSize(new Dimension(320, 32));
        form.add(accountField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        form.add(Theme.formLabel("Password:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 1.0;
        passwordInput = new PasswordInput();
        passwordInput.setPreferredSize(new Dimension(320, 32));
        form.add(passwordInput, gc);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton signIn = Theme.primaryButton("SIGN IN");
        JButton clear = Theme.secondaryButton("CLEAR");
        JButton signUp = Theme.primaryButton("SIGN UP");
        buttons.add(signIn);
        buttons.add(clear);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        form.add(buttons, gc);

        JPanel signUpPanel = new JPanel();
        signUpPanel.setOpaque(false);
        signUpPanel.add(signUp);
        gc.gridy = 3;
        form.add(signUpPanel, gc);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        signIn.addActionListener(e -> doLogin());
        clear.addActionListener(e -> {
            accountField.setText("");
            passwordInput.clear();
        });
        signUp.addActionListener(e -> {
            new SignUpGUI().setVisible(true);
            dispose();
        });
    }

    private void doLogin() {
        String accountNumber = accountField.getText().trim();
        String password = passwordInput.getText();

        try {
            if (accountNumber.isEmpty() || password.isEmpty()) {
                throw new InvalidLoginException("Account number and password are required.");
            }
            Account account = DatabaseHandler.authenticate(accountNumber, password);
            if (account == null) {
                throw new InvalidLoginException("Invalid account number or password.");
            }
            FileHandler.logEvent("LOGIN " + accountNumber);
            new DashboardGUI(account).setVisible(true);
            dispose();
        } catch (InvalidLoginException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Login failed", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
