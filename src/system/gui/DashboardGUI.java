package system.gui;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.AccountNotFoundException;
import system.models.Account;
import system.models.Customer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

public class DashboardGUI extends JFrame {

    private Account account;
    private final JLabel balanceLabel;
    private final JLabel welcomeLabel;

    public DashboardGUI(Account account) {
        this.account = account;

        setTitle("ET Banking system - Dashboard");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK_TEAL);
        header.setBorder(Theme.panelBorder());

        welcomeLabel = new JLabel("Welcome", SwingConstants.LEFT);
        welcomeLabel.setForeground(Theme.TEXT_WHITE);
        welcomeLabel.setFont(Theme.FONT_HEADING);

        balanceLabel = new JLabel("Balance: 0.00 " + Theme.CURRENCY, SwingConstants.RIGHT);
        balanceLabel.setForeground(Theme.ACCENT_YELLOW);
        balanceLabel.setFont(Theme.FONT_HEADING);

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(balanceLabel, BorderLayout.EAST);

        JPanel body = new JPanel(new GridLayout(5, 2, 20, 20));
        body.setBackground(Theme.BG_TEAL);
        body.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 60, 40, 60));

        body.add(dashboardButton("CHECK BALANCE", e -> showBalance()));
        body.add(dashboardButton("DEPOSIT MONEY", e -> openDeposit()));
        body.add(dashboardButton("WITHDRAW MONEY", e -> openWithdraw()));
        body.add(dashboardButton("TRANSFER MONEY", e -> openTransfer()));
        body.add(dashboardButton("TRANSACTION HISTORY", e -> openHistory()));
        body.add(dashboardButton("LOAN MANAGEMENT", e -> openLoanManagement()));
        body.add(dashboardButton("MY ACCOUNTS", e -> openMyAccounts()));
        body.add(dashboardButton("DELETE ACCOUNT", e -> deleteAccount()));
        body.add(dashboardButton("LOGOUT", e -> logout()));

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        refreshHeader();
    }

    private JButton dashboardButton(String text, java.awt.event.ActionListener listener) {
        JButton b = Theme.primaryButton(text);
        b.setPreferredSize(new Dimension(220, 80));
        b.setBackground(new Color(20, 20, 20));
        b.addActionListener(listener);
        return b;
    }

    private void refreshHeader() {
        try {
            Customer c = DatabaseHandler.getCustomer(account.getCustomerId());
            String name = (c != null) ? c.getFullName() : account.getCustomerId();
            welcomeLabel.setText("Welcome, " + name + "  (" + account.getAccountNumber()
                    + " - " + account.getAccountType() + ")");
            balanceLabel.setText("Balance: " + Theme.money(account.getBalance()));
        } catch (SQLException ex) {
            welcomeLabel.setText("Welcome, " + account.getAccountNumber());
        }
    }

    public void reloadAccount() {
        try {
            this.account = DatabaseHandler.getAccount(account.getAccountNumber());
            refreshHeader();
        } catch (SQLException | AccountNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Could not reload account: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Account getAccount() {
        return account;
    }

    private void showBalance() {
        JOptionPane.showMessageDialog(this,
                "Account Number: " + account.getAccountNumber()
                        + "\nType: " + account.getAccountType()
                        + "\nCurrent Balance: " + Theme.money(account.getBalance()),
                "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openDeposit() {
        new DepositGUI(this).setVisible(true);
    }

    private void openWithdraw() {
        new WithdrawGUI(this).setVisible(true);
    }

    private void openTransfer() {
        new TransferGUI(this).setVisible(true);
    }

    private void openHistory() {
        new TransactionHistoryGUI(account).setVisible(true);
    }

    private void openLoanManagement() {
        new LoanGUI(this).setVisible(true);
    }

    private void openMyAccounts() {
        new AccountsGUI(this).setVisible(true);
    }

    private void deleteAccount() {
        if (account.getLoanBalance() > 0) {
            JOptionPane.showMessageDialog(this, "Cannot delete account until your loan is fully repaid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (account.getBalance() > 0) {
            int choice = JOptionPane.showConfirmDialog(this, "You have remaining funds (" + Theme.money(account.getBalance()) + "). Do you acknowledge that you will permanently lose this balance if you proceed?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your account? This action cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String customerId = account.getCustomerId();
                DatabaseHandler.deleteAccount(account.getAccountNumber());
                
                List<Account> accounts = DatabaseHandler.getAccountsForCustomer(customerId);
                if (accounts.isEmpty()) {
                    DatabaseHandler.deleteCustomer(customerId);
                }

                FileHandler.logEvent("ACCOUNT_DELETED " + account.getAccountNumber());
                JOptionPane.showMessageDialog(this, "Account successfully deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                new LoginGUI().setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        FileHandler.logEvent("LOGOUT " + account.getAccountNumber());
        new LoginGUI().setVisible(true);
        dispose();
    }
}
