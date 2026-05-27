package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;

    public SavingsAccount(String accountNumber, String customerId, double balance, String password) {
        this(accountNumber, customerId, customerId, balance, 0.05, 500.0, password);
    }

    public SavingsAccount(String accountNumber, String customerId, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, balance, loanBalance, 0.05, 500.0, password);
    }

    public SavingsAccount(String accountNumber, String customerId, String customerName,
            double balance, double interestRate, double minimumBalance, String password) {
        this(accountNumber, customerId, customerName, balance, 0.0, interestRate, minimumBalance, password);
    }

    public SavingsAccount(String accountNumber, String customerId, String customerName,
            double balance, double loanBalance, double interestRate,
            double minimumBalance, String password) {
        super(accountNumber, customerId, customerName, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    // Savings accounts protect a minimum balance and earn interest separately.
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        if (amount > balance - minimumBalance) {
            throw new InsufficientFundsException("Savings account must keep a minimum balance of "
                    + minimumBalance + ".");
        }
        balance -= amount;
    }

    public void applyInterest() {
        balance += balance * interestRate;
    }

    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nMinimum Balance: " + minimumBalance
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
