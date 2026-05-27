package system.operations;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InvalidAmountException;
import system.models.Account;
import system.models.Transaction;

import java.sql.SQLException;

public class Deposit {

    private final Account account;
    private final double amount;

    public Deposit(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    public void execute() throws InvalidAmountException, SQLException {
        account.deposit(amount);
        DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance());
        Transaction t = new Transaction(account.getAccountNumber(), "DEPOSIT",
                amount, account.getBalance(), null);
        DatabaseHandler.recordTransaction(t);
        account.addTransaction(t);
        FileHandler.logTransaction(t);
    }
}
