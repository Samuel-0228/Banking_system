package atmsystem.operations;

import atmsystem.database.DatabaseHandler;
import atmsystem.database.FileHandler;
import atmsystem.exceptions.InvalidAmountException;
import atmsystem.models.Account;
import atmsystem.models.Transaction;

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
