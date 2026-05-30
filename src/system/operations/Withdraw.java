package system.operations;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;
import system.models.Account;
import system.models.Transaction;

import java.sql.SQLException;

/**
 * Handles the operation of withdrawing funds from a bank account.
 * Updates the account balance, records the transaction, and logs the event.
 */
public class Withdraw {

    private final Account account;
    private final double amount;

    /**
     * Constructs a Withdraw operation.
     *
     * @param account The account to withdraw from.
     * @param amount  The amount to withdraw.
     */
    public Withdraw(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    /**
     * Executes the withdrawal transaction.
     * Updates the account model, database balance, transaction history, and text log.
     *
     * @throws InvalidAmountException     If the withdrawal amount is zero or negative.
     * @throws InsufficientFundsException If the account does not have sufficient funds (or exceeds overdraft).
     * @throws SQLException               If a database error occurs during updates.
     */
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
