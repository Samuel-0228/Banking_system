package system.gui;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;
import system.models.Account;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoanGUI extends JFrame {

    private final DashboardGUI dashboard;
    private final Account account;

    public LoanGUI(DashboardGUI dashboard) {
        this.dashboard = dashboard;
        this.account = dashboard.getAccount();

        setTitle("ET Banking system - Loan Management");
        setSize(400, 350);
        setLocationRelativeTo(dashboard);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("LOAN MANAGEMENT"));

        JPanel body = new JPanel(new GridLayout(3, 1, 15, 15));
        body.setBackground(Theme.BG_TEAL);
        body.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton requestBtn = Theme.primaryButton("REQUEST LOAN");
        JButton remainingBtn = Theme.primaryButton("REMAINING LOAN");
        JButton payBtn = Theme.primaryButton("PAY LOAN");

        requestBtn.addActionListener(e -> requestLoan());
        remainingBtn.addActionListener(e -> showRemainingLoan());
        payBtn.addActionListener(e -> payLoan());

        body.add(requestBtn);
        body.add(remainingBtn);
        body.add(payBtn);

        JPanel footer = new JPanel();
        footer.setBackground(Theme.BG_TEAL);
        JButton backBtn = Theme.secondaryButton("BACK");
        backBtn.addActionListener(e -> dispose());
        footer.add(backBtn);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void requestLoan() {
        String input = JOptionPane.showInputDialog(this, "Enter loan amount:", "Request Loan", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            account.applyForLoan(amount);
            DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance());
            DatabaseHandler.updateLoanBalance(account.getAccountNumber(), account.getLoanBalance());
            
            if (!account.getTransactions().isEmpty()) {
                DatabaseHandler.recordTransaction(account.getTransactions().get(account.getTransactions().size() - 1));
            }
            FileHandler.logEvent("LOAN_APPROVED " + account.getAccountNumber() + " amount=" + amount);

            JOptionPane.showMessageDialog(this, "Loan approved successfully! New balance: " + Theme.money(account.getBalance()), "Success", JOptionPane.INFORMATION_MESSAGE);
            dashboard.reloadAccount();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidAmountException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRemainingLoan() {
        JOptionPane.showMessageDialog(this, "Your remaining loan balance is: " + Theme.money(account.getLoanBalance()), "Remaining Loan", JOptionPane.INFORMATION_MESSAGE);
    }

    private void payLoan() {
        if (account.getLoanBalance() <= 0) {
            JOptionPane.showMessageDialog(this, "You do not have any outstanding loan.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter payment amount (Outstanding: " + Theme.money(account.getLoanBalance()) + "):", "Pay Loan", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            account.payLoan(amount);
            DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance());
            DatabaseHandler.updateLoanBalance(account.getAccountNumber(), account.getLoanBalance());
            
            if (!account.getTransactions().isEmpty()) {
                DatabaseHandler.recordTransaction(account.getTransactions().get(account.getTransactions().size() - 1));
            }
            FileHandler.logEvent("LOAN_PAYMENT " + account.getAccountNumber() + " amount=" + amount);

            JOptionPane.showMessageDialog(this, "Loan payment successful! Remaining loan: " + Theme.money(account.getLoanBalance()), "Success", JOptionPane.INFORMATION_MESSAGE);
            dashboard.reloadAccount();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidAmountException | InsufficientFundsException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
