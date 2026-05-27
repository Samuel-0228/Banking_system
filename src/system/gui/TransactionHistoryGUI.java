package system.gui;

import system.database.DatabaseHandler;
import system.models.Account;
import system.models.Transaction;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionHistoryGUI extends JDialog {

    public TransactionHistoryGUI(Account account) {
        super((java.awt.Frame) null, "Transaction History", true);
        setSize(780, 460);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_TEAL);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Theme.BG_TEAL);
        header.setBorder(Theme.panelBorder());
        header.add(Theme.titleLabel("TRANSACTION HISTORY"));

        String[] columns = { "ID", "Type", "Amount (" + Theme.CURRENCY + ")",
                "Balance After (" + Theme.CURRENCY + ")", "Target", "Date" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<Transaction> transactions = DatabaseHandler.getTransactions(account.getAccountNumber());
            for (Transaction t : transactions) {
                model.addRow(new Object[] {
                        t.getTransactionId(),
                        t.getTransactionType(),
                        String.format("%.2f", t.getAmount()),
                        String.format("%.2f", t.getBalanceAfter()),
                        t.getTargetAccount() != null ? t.getTargetAccount() : "-",
                        t.getTransactionDate() != null ? fmt.format(t.getTransactionDate()) : "-"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JButton close = Theme.primaryButton("CLOSE");
        close.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setBackground(Theme.BG_TEAL);
        footer.add(close);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}
