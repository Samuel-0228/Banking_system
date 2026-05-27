package system.models;

import system.exceptions.AccountNotFoundException;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected String customerId;
    protected String customerName;
    protected double balance;
    protected double loanBalance;
    protected String password;
    protected final List<Transaction> transactions;

    public Account(String accountNumber, String customerId, double balance, String password) {
        this(accountNumber, customerId, customerId, balance, 0.0, password);
    }

    public Account(String accountNumber, String customerId, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, balance, loanBalance, password);
    }

    public Account(String accountNumber, String customerId, String customerName,
            double balance, String password) {
        this(accountNumber, customerId, customerName, balance, 0.0, password);
    }

    public Account(String accountNumber, String customerId, String customerName,
            double balance, double loanBalance, String password) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.customerName = (customerName == null || customerName.trim().isEmpty())
                ? customerId
                : customerName;
        this.balance = balance;
        this.loanBalance = loanBalance;
        this.password = password;
        this.transactions = new ArrayList<>();
    }

    public abstract String getAccountType();

    public abstract double getInterestRate();

    /**
     * Base deposit logic is shared, while subclasses can add account-specific
     * rules.
     */
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    /**
     * Subclasses override this when they need different withdrawal rules.
     */
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

    public void payLoan(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Payment amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        if (amount > loanBalance) {
            throw new InvalidAmountException("Cannot pay more than the outstanding loan balance.");
        }
        balance -= amount;
        loanBalance -= amount;
        addTransaction(new Transaction(accountNumber, "LOAN_PAYMENT",
                amount, balance, null));
    }

    public boolean verifyPassword(String input) {
        return password != null && password.equals(input);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getBalance() {
        return balance;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getLoanBalance() {
        return loanBalance;
    }

    public String getPassword() {
        return password;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setLoanBalance(double loanBalance) {
        this.loanBalance = loanBalance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    /**
     * Subclasses append their own rules so account details read differently per
     * account type.
     */
    public String displayAccountInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Account Number: ").append(accountNumber).append('\n');
        info.append("Customer Name: ").append(customerName).append('\n');
        info.append("Customer ID: ").append(customerId).append('\n');
        info.append("Account Type: ").append(getAccountType()).append('\n');
        info.append("Balance: ").append(balance).append('\n');
        info.append("Loan Balance: ").append(loanBalance);
        return info.toString();
    }

    @Override
    public String toString() {
        return accountNumber + " (" + getAccountType() + ")";
    }
}
