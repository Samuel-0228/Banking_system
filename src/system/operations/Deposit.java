package system.operations;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InvalidAmountException;
import system.models.Account;
import system.models.Transaction;

import java.sql.SQLException;

/**
 * Handles the operation of depositing funds into a bank account.
 * Updates the account balance, records the transaction, and logs the event.
 */
public class Deposit {

    private final Account account;
    private final double amount;

    /**
     * Constructs a Deposit operation.
     *
     * @param account The account to deposit into.
     * @param amount  The amount to deposit.
     */
    public Deposit(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    /**
     * Executes the deposit transaction.
     * Updates the account model, database balance, transaction history, and text log.
     *
     * @throws InvalidAmountException If the deposit amount is zero or negative.
     * @throws SQLException           If a database error occurs during updates.
     */
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
