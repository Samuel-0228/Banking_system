package system.main;

import system.database.DatabaseHandler;
import system.gui.BankingSystemGUI;
import system.models.Bank;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            Bank bank = new Bank("ET Banking system", "Main Branch", "ATM001");
            bank.addBranch("Main Branch");
            bank.addBranch("City Branch");

            if (!DatabaseHandler.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the database.\n\n"
                                + "1. Make sure XAMPP MySQL is running.\n"
                                + "2. Make sure the 'banking_system' database has been created\n"
                                + "   by importing database/banking_system.sql.\n"
                                + "3. Make sure mysql-connector-j-x.x.x.jar is added to the\n"
                                + "   project libraries (Right-click project → Properties → Libraries → Add JAR).\n",
                        "Database connection failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    bank.refreshData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Connected to the database, but failed to preload bank data.\n" + ex.getMessage(),
                            "Startup warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            new BankingSystemGUI(bank).setVisible(true);
        });
    }
}
