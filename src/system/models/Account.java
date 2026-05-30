package system.models;

import system.exceptions.AccountNotFoundException;
import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a generic bank account.
 * Provides the base structure and common functionalities (e.g., deposit, withdraw, transfer)
 * for all specific account types in the banking system.
 */
public abstract class Account {
    protected String accountNumber;
    protected String customerId;
    protected String customerName;
    protected String branch;
    protected double balance;
    protected double loanBalance;
    protected String password;
    protected final List<Transaction> transactions;

    /**
     * Constructs a new Account with basic details.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer owning this account.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param password      The password for accessing the account.
     */
    public Account(String accountNumber, String customerId, String branch, double balance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, 0.0, password);
    }

    public Account(String accountNumber, String customerId, String branch, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, loanBalance, password);
    }

    public Account(String accountNumber, String customerId, String customerName, String branch,
            double balance, String password) {
        this(accountNumber, customerId, customerName, branch, balance, 0.0, password);
    }

    public Account(String accountNumber, String customerId, String customerName, String branch,
            double balance, double loanBalance, String password) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.customerName = (customerName == null || customerName.trim().isEmpty())
                ? customerId
                : customerName;
        this.branch = branch;
        this.balance = balance;
        this.loanBalance = loanBalance;
        this.password = password;
        this.transactions = new ArrayList<>();
    }

    /**
     * Retrieves the specific type of the account.
     *
     * @return The account type (e.g., "Savings", "Current").
     */
    public abstract String getAccountType();

    /**
     * Retrieves the interest rate applicable to the account.
     *
     * @return The interest rate as a decimal.
     */
    public abstract double getInterestRate();

    /**
     * Deposits a specified amount into the account.
     * Base deposit logic is shared, while subclasses can add account-specific rules.
     *
     * @param amount The amount to deposit.
     * @throws InvalidAmountException If the amount is zero or negative.
     */
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        this.balance += amount;
    }

    /**
     * Withdraws a specified amount from the account.
     * Subclasses override this when they need different withdrawal rules.
     *
     * @param amount The amount to withdraw.
     * @throws InvalidAmountException     If the amount is zero or negative.
     * @throws InsufficientFundsException If the balance is less than the withdrawal amount.
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

    /**
     * Transfers a specified amount to another account.
     *
     * @param receiver The target account to receive the funds.
     * @param amount   The amount to transfer.
     * @throws InvalidAmountException     If the amount is invalid or transferring to the same account.
     * @throws InsufficientFundsException If the source account lacks sufficient funds.
     * @throws AccountNotFoundException   If the receiver account is null.
     */
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

    /**
     * Applies for a loan of the specified amount.
     *
     * @param amount The loan amount requested.
     * @throws InvalidAmountException If the requested amount is zero or negative.
     */
    public void applyForLoan(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Loan amount must be greater than zero.");
        }
        loanBalance += amount;
        balance += amount;
        addTransaction(new Transaction(accountNumber, "LOAN_APPROVED",
                amount, balance, null));
    }

    /**
     * Pays off a portion or all of the outstanding loan balance.
     *
     * @param amount The amount to pay towards the loan.
     * @throws InvalidAmountException     If the amount is invalid or exceeds the loan balance.
     * @throws InsufficientFundsException If the account balance is less than the payment amount.
     */
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

    /**
     * Verifies if the provided password matches the account's password.
     *
     * @param input The password to check.
     * @return True if the password matches, false otherwise.
     */
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

    public String getBranch() {
        return branch;
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

    public void setBranch(String branch) {
        this.branch = branch;
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
     * Returns a formatted string containing the account's detailed information.
     * Subclasses append their own rules so account details read differently per account type.
     *
     * @return A string containing account details.
     */
    public String displayAccountInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Account Number: ").append(accountNumber).append('\n');
        info.append("Customer Name: ").append(customerName).append('\n');
        info.append("Customer ID: ").append(customerId).append('\n');
        info.append("Branch: ").append(branch).append('\n');
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
