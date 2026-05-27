package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

public class CurrentAccount extends Account {
    private final double interestRate;
    private final double overdraftLimit;

    public CurrentAccount(String accountNumber, String customerId, double balance, String password) {
        this(accountNumber, customerId, customerId, balance, 0.01, 500.0, password);
    }

    public CurrentAccount(String accountNumber, String customerId, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, balance, loanBalance, 0.01, 500.0, password);
    }

    public CurrentAccount(String accountNumber, String customerId, String customerName,
            double balance, double interestRate, double overdraftLimit, String password) {
        this(accountNumber, customerId, customerName, balance, 0.0, interestRate, overdraftLimit, password);
    }

    public CurrentAccount(String accountNumber, String customerId, String customerName,
            double balance, double loanBalance, double interestRate,
            double overdraftLimit, String password) {
        super(accountNumber, customerId, customerName, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "Current";
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    // Current accounts allow overdrafts rather than blocking the withdrawal
    // outright.
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        if (amount > balance + overdraftLimit) {
            throw new InsufficientFundsException("Insufficient funds. Overdraft limit: "
                    + overdraftLimit + ", Available balance: " + balance
                    + ", Requested amount: " + amount);
        }
        this.balance -= amount;
    }

    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nOverdraft Limit: " + overdraftLimit
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
