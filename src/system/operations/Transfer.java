package system.operations;

import system.database.DatabaseHandler;
import system.database.FileHandler;
import system.exceptions.AccountNotFoundException;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;
import system.models.Account;
import system.models.Transaction;

import java.sql.SQLException;

/**
 * Handles the operation of transferring funds between two bank accounts.
 * Ensures data consistency across sender and receiver accounts, databases, and logs.
 */
public class Transfer {

    private final Account sender;
    private final String receiverAccountNumber;
    private final double amount;

    /**
     * Constructs a Transfer operation.
     *
     * @param sender                The account initiating the transfer.
     * @param receiverAccountNumber The account number of the receiving account.
     * @param amount                The amount to transfer.
     */
    public Transfer(Account sender, String receiverAccountNumber, double amount) {
        this.sender = sender;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
    }

    /**
     * Executes the transfer transaction.
     * Validates accounts, checks balances, withdraws from sender, deposits to receiver,
     * and records all related transactions and logs.
     *
     * @throws InvalidAmountException     If the amount is zero, negative, or transferring to the same account.
     * @throws InsufficientFundsException If the sender does not have enough funds.
     * @throws AccountNotFoundException   If the receiver account cannot be found.
     * @throws SQLException               If a database error occurs.
     */
    public void execute() throws InvalidAmountException, InsufficientFundsException,
            AccountNotFoundException, SQLException {
        if (sender.getAccountNumber().equals(receiverAccountNumber)) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }
        Account receiver = DatabaseHandler.getAccount(receiverAccountNumber);

        sender.withdraw(amount);
        receiver.setBalance(receiver.getBalance() + amount);

        DatabaseHandler.updateBalance(sender.getAccountNumber(), sender.getBalance());
        DatabaseHandler.updateBalance(receiver.getAccountNumber(), receiver.getBalance());

        Transaction outgoing = new Transaction(sender.getAccountNumber(), "TRANSFER_OUT",
                amount, sender.getBalance(), receiver.getAccountNumber());
        Transaction incoming = new Transaction(receiver.getAccountNumber(), "TRANSFER_IN",
                amount, receiver.getBalance(), sender.getAccountNumber());

        DatabaseHandler.recordTransaction(outgoing);
        DatabaseHandler.recordTransaction(incoming);
        sender.addTransaction(outgoing);
        receiver.addTransaction(incoming);
        FileHandler.logTransaction(outgoing);
        FileHandler.logTransaction(incoming);
    }
}
