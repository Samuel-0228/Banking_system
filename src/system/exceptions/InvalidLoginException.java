package system.exceptions;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 */
public class InvalidLoginException extends BankingException {

    /**
     * Constructs the exception with a detailed message.
     *
     * @param message The detail message.
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
