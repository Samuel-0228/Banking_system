package atmsystem.models;

import atmsystem.exceptions.InsufficientFundsException;
import atmsystem.exceptions.InvalidAmountException;

public class CurrentAccount extends Account {
    private static final double INTEREST_RATE = 0.01;
    private static final double OVERDRAFT_LIMIT = 500.0;

    public CurrentAccount(String accountNumber, String customerId, double balance, String password) {
        super(accountNumber, customerId, balance, password);
    }

    public CurrentAccount(String accountNumber, String customerId, double balance,
                          double loanBalance, String password) {
        super(accountNumber, customerId, balance, loanBalance, password);
    }

    @Override
    public String getAccountType() { return "Current"; }

    @Override
    public double getInterestRate() { return INTEREST_RATE; }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        if (amount > balance + OVERDRAFT_LIMIT) {
            throw new InsufficientFundsException(balance, amount);
        }
        this.balance -= amount;
    }
}
