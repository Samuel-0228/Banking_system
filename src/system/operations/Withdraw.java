package system.operations;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;
import system.models.Account;
import system.models.Transaction;

import java.sql.SQLException;

public class Withdraw {

    private final Account account;
    private final double amount;

    public Withdraw(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    public void execute() throws InvalidAmountException, InsufficientFundsException, SQLException {
        account.withdraw(amount);
        DatabaseHandler.updateBalance(account.getAccountNumber(), account.getBalance());
        Transaction t = new Transaction(account.getAccountNumber(), "WITHDRAW",
                amount, account.getBalance(), null);
        DatabaseHandler.recordTransaction(t);
        account.addTransaction(t);
        FileHandler.logTransaction(t);
    }
}
