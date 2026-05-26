package atmsystem.exceptions;

public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(double available, double requested) {
        super("Insufficient funds. Available balance: " + available
                + ", Requested amount: " + requested);
    }
}
