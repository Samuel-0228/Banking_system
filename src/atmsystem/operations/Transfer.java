package atmsystem.operations;

import atmsystem.database.DatabaseHandler;
import atmsystem.database.FileHandler;
import atmsystem.exceptions.AccountNotFoundException;
import atmsystem.exceptions.InsufficientFundsException;
import atmsystem.exceptions.InvalidAmountException;
import atmsystem.models.Account;
import atmsystem.models.Transaction;

import java.sql.SQLException;

public class Transfer {

    private final Account sender;
    private final String receiverAccountNumber;
    private final double amount;

    public Transfer(Account sender, String receiverAccountNumber, double amount) {
        this.sender = sender;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
    }

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
