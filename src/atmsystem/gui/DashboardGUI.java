package atmsystem.gui;

import atmsystem.database.DatabaseHandler;
import atmsystem.database.FileHandler;
import atmsystem.exceptions.AccountNotFoundException;
import atmsystem.models.Account;
import atmsystem.models.Customer;

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

public class DashboardGUI extends JFrame {

    private Account account;
    private final JLabel balanceLabel;
    private final JLabel welcomeLabel;

    public DashboardGUI(Account account) {
        this.account = account;

        setTitle("ATM System - Dashboard");
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

        JPanel body = new JPanel(new GridLayout(3, 2, 20, 20));
        body.setBackground(Theme.BG_TEAL);
        body.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 60, 40, 60));

        body.add(dashboardButton("CHECK BALANCE", e -> showBalance()));
        body.add(dashboardButton("DEPOSIT MONEY", e -> openDeposit()));
        body.add(dashboardButton("WITHDRAW MONEY", e -> openWithdraw()));
        body.add(dashboardButton("TRANSFER MONEY", e -> openTransfer()));
        body.add(dashboardButton("TRANSACTION HISTORY", e -> openHistory()));
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

    public Account getAccount() { return account; }

    private void showBalance() {
        JOptionPane.showMessageDialog(this,
                "Account Number: " + account.getAccountNumber()
                        + "\nType: " + account.getAccountType()
                        + "\nCurrent Balance: " + Theme.money(account.getBalance()),
                "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openDeposit() { new DepositGUI(this).setVisible(true); }
    private void openWithdraw() { new WithdrawGUI(this).setVisible(true); }
    private void openTransfer() { new TransferGUI(this).setVisible(true); }
    private void openHistory() { new TransactionHistoryGUI(account).setVisible(true); }

    private void logout() {
        FileHandler.logEvent("LOGOUT " + account.getAccountNumber());
        new LoginGUI().setVisible(true);
        dispose();
    }
}
