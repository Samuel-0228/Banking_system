package system.exceptions;

/**
 * Exception thrown when an account cannot be located in the database.
 */
public class AccountNotFoundException extends BankingException {

    /**
     * Constructs the exception for a specific account number.
     *
     * @param accountNumber The account number that was not found.
     */
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
