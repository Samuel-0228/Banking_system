package system.exceptions;

/**
 * Exception thrown when a transaction amount is invalid (e.g., negative or zero).
 */
public class InvalidAmountException extends BankingException {

    /**
     * Constructs the exception with a detailed message.
     *
     * @param message The detail message explaining why the amount is invalid.
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}
