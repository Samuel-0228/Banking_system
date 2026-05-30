package system.exceptions;

/**
 * Exception thrown when an account does not have enough funds to complete a transaction.
 */
public class InsufficientFundsException extends BankingException {

    /**
     * Constructs the exception with a specific detail message.
     *
     * @param message The detail message.
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructs the exception by detailing the available balance versus the requested amount.
     *
     * @param available The available balance in the account.
     * @param requested The amount requested for the transaction.
     */
    public InsufficientFundsException(double available, double requested) {
        super("Insufficient funds. Available balance: " + available
                + ", Requested amount: " + requested);
    }
}
