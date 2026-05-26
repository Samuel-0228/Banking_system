package atmsystem.models;

import atmsystem.exceptions.AccountNotFoundException;
import atmsystem.exceptions.InsufficientFundsException;
import atmsystem.exceptions.InvalidAmountException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String customerId;
    protected double balance;
    protected double loanBalance;
    protected String password;
    protected final List<Transaction> transactions;

    public Account(String accountNumber, String customerId, double balance, String password) {
        this(accountNumber, customerId, balance, 0.0, password);
    }

    public Account(String accountNumber, String customerId, double balance,
                   double loanBalance, String password) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
        this.loanBalance = loanBalance;
        this.password = password;
        this.transactions = new ArrayList<>();
    }

    public abstract String getAccountType();
    public abstract double getInterestRate();

    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        this.balance -= amount;
    }

    public void transfer(Account receiver, double amount)
            throws InvalidAmountException, InsufficientFundsException, AccountNotFoundException {
        if (receiver == null) {
            throw new AccountNotFoundException("Unknown receiver account");
        }
        if (accountNumber.equals(receiver.getAccountNumber())) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }

        withdraw(amount);
        receiver.balance += amount;

        Timestamp now = new Timestamp(System.currentTimeMillis());
        addTransaction(new Transaction(0, accountNumber, "TRANSFER_OUT",
                amount, balance, receiver.getAccountNumber(), now));
        receiver.addTransaction(new Transaction(0, receiver.getAccountNumber(), "TRANSFER_IN",
                amount, receiver.getBalance(), accountNumber, now));
    }

    public void applyForLoan(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Loan amount must be greater than zero.");
        }
        loanBalance += amount;
        balance += amount;
        addTransaction(new Transaction(accountNumber, "LOAN_APPROVED",
                amount, balance, null));
    }

    public boolean verifyPassword(String input) {
        return password != null && password.equals(input);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public double getBalance() { return balance; }
    public double getLoanBalance() { return loanBalance; }
    public String getPassword() { return password; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public void setBalance(double balance) { this.balance = balance; }
    public void setLoanBalance(double loanBalance) { this.loanBalance = loanBalance; }
    public void setPassword(String password) { this.password = password; }
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    @Override
    public String toString() {
        return accountNumber + " (" + getAccountType() + ")";
    }
}
