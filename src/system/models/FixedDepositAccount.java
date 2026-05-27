package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

import java.time.LocalDate;

public class FixedDepositAccount extends Account {
    private final double interestRate;
    private final int lockPeriodMonths;
    private LocalDate maturityDate;

    public FixedDepositAccount(String accountNumber, String customerId, double balance, String password) {
        this(accountNumber, customerId, customerId, balance, 0.10, 12,
                LocalDate.now().plusMonths(12), password);
    }

    public FixedDepositAccount(String accountNumber, String customerId, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, balance, loanBalance, 0.10, 12,
                LocalDate.now().plusMonths(12), password);
    }

    public FixedDepositAccount(String accountNumber, String customerId, String customerName,
            double balance, double interestRate, int lockPeriodMonths,
            LocalDate maturityDate, String password) {
        this(accountNumber, customerId, customerName, balance, 0.0, interestRate,
                lockPeriodMonths, maturityDate, password);
    }

    public FixedDepositAccount(String accountNumber, String customerId, String customerName,
            double balance, double loanBalance, double interestRate,
            int lockPeriodMonths, LocalDate maturityDate, String password) {
        super(accountNumber, customerId, customerName, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.lockPeriodMonths = lockPeriodMonths;
        this.maturityDate = (maturityDate == null)
                ? LocalDate.now().plusMonths(lockPeriodMonths)
                : maturityDate;
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit";
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public int getLockPeriodMonths() {
        return lockPeriodMonths;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    // Fixed deposits lock money until maturity; any top-up renews the lock period.
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
        maturityDate = LocalDate.now().plusMonths(lockPeriodMonths);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        if (LocalDate.now().isBefore(maturityDate)) {
            throw new InsufficientFundsException("Fixed deposit is locked until "
                    + maturityDate + ".");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
    }

    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nLock Period (months): " + lockPeriodMonths
                + "\nMaturity Date: " + maturityDate
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
