package system.gui;

import system.database.DatabaseHandler;
import system.models.Account;
import system.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AccountsGUI extends JFrame {

    private final DashboardGUI dashboard;
    private final Account currentAccount;

    public AccountsGUI(DashboardGUI dashboard) {
        this.dashboard = dashboard;
        this.currentAccount = dashboard.getAccount();

        setTitle("ET Banking system - My Accounts");
        setSize(500, 450);
        setLocationRelativeTo(dashboard);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("MY ACCOUNTS"));

        JPanel body = new JPanel();
        body.setBackground(Theme.BG_TEAL);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        
        try {
            List<Account> accounts = DatabaseHandler.getAccountsForCustomer(currentAccount.getCustomerId());
            for (Account acc : accounts) {
                JPanel accPanel = new JPanel(new BorderLayout());
                accPanel.setBackground(Theme.BG_DARK_TEAL);
                accPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 20, 10, 20),
                        BorderFactory.createLineBorder(Theme.ACCENT_YELLOW, 1)));
                
                String info = String.format("<html><b>%s</b><br>Type: %s<br>Interest Rate: %.0f%%<br>Balance: %s</html>",
                        acc.getAccountNumber(), acc.getAccountType(), acc.getInterestRate() * 100, Theme.money(acc.getBalance()));
                
                JLabel accLabel = new JLabel(info);
                accLabel.setForeground(Theme.TEXT_WHITE);
                accLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                accPanel.add(accLabel, BorderLayout.CENTER);
                
                body.add(accPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load accounts.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footer.setBackground(Theme.BG_TEAL);
        
        JButton createBtn = Theme.primaryButton("CREATE NEW ACCOUNT");
        JButton backBtn = Theme.secondaryButton("BACK");
        
        createBtn.addActionListener(e -> {
            try {
                Customer customer = DatabaseHandler.getCustomer(currentAccount.getCustomerId());
                if (customer != null) {
                    new AccountCreationGUI(customer).setVisible(true);
                    dispose(); // They can go back from AccountCreation to login, or we can adjust that.
                    // Actually, AccountCreationGUI closes and opens LoginGUI right now.
                    // For a better UX, it should just dispose and they can log back in.
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> dispose());
        
        footer.add(createBtn);
        footer.add(backBtn);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}
