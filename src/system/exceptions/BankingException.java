package system.exceptions;

/**
 * Base exception class for all custom banking-related errors.
 */
public class BankingException extends Exception {

    /**
     * Constructs a BankingException with a detailed message.
     *
     * @param message The detail message.
     */
    public BankingException(String message) {
        super(message);
    }
}
