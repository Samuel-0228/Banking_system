package system.exceptions;

public class InvalidLoginException extends BankingException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
