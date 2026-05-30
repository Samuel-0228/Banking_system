package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

/**
 * Represents a current account in the banking system.
 * Allows overdrafts up to a specified limit rather than blocking withdrawals outright.
 */
public class CurrentAccount extends Account {
    private final double interestRate;
    private final double overdraftLimit;

    /**
     * Constructs a CurrentAccount with default interest rate and overdraft limit.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param password      The password for accessing the account.
     */
    public CurrentAccount(String accountNumber, String customerId, String branch, double balance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, 0.01, 500.0, password);
    }

    /**
     * Constructs a CurrentAccount with an existing loan balance.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param loanBalance   The outstanding loan balance on the account.
     * @param password      The password for accessing the account.
     */
    public CurrentAccount(String accountNumber, String customerId, String branch, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, loanBalance, 0.01, 500.0, password);
    }

    /**
     * Constructs a CurrentAccount with custom interest rate and overdraft limit.
     *
     * @param accountNumber  The unique identifier for the account.
     * @param customerId     The unique identifier for the customer.
     * @param customerName   The name of the customer.
     * @param branch         The branch where the account was opened.
     * @param balance        The initial balance of the account.
     * @param interestRate   The interest rate applicable to the account.
     * @param overdraftLimit The maximum overdraft limit allowed.
     * @param password       The password for accessing the account.
     */
    public CurrentAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double interestRate, double overdraftLimit, String password) {
        this(accountNumber, customerId, customerName, branch, balance, 0.0, interestRate, overdraftLimit, password);
    }

    /**
     * Constructs a CurrentAccount with all details including loan balance, custom interest rate, and overdraft limit.
     *
     * @param accountNumber  The unique identifier for the account.
     * @param customerId     The unique identifier for the customer.
     * @param customerName   The name of the customer.
     * @param branch         The branch where the account was opened.
     * @param balance        The initial balance of the account.
     * @param loanBalance    The outstanding loan balance on the account.
     * @param interestRate   The interest rate applicable to the account.
     * @param overdraftLimit The maximum overdraft limit allowed.
     * @param password       The password for accessing the account.
     */
    public CurrentAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double loanBalance, double interestRate,
            double overdraftLimit, String password) {
        super(accountNumber, customerId, customerName, branch, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Retrieves the specific type of the account.
     *
     * @return A string representing the account type ("Current").
     */
    @Override
    public String getAccountType() {
        return "Current";
    }

    /**
     * Retrieves the interest rate applicable to the current account.
     *
     * @return The interest rate as a decimal.
     */
    @Override
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Retrieves the overdraft limit for this account.
     *
     * @return The maximum allowable overdraft limit.
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Deposits a specified amount into the current account.
     * Current accounts allow overdrafts rather than blocking the withdrawal outright.
     *
     * @param amount The amount to deposit.
     * @throws InvalidAmountException If the amount is zero or negative.
     */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
    }

    /**
     * Withdraws a specified amount from the current account.
     * Allows overdrafts up to the specified overdraft limit.
     *
     * @param amount The amount to withdraw.
     * @throws InvalidAmountException     If the amount is zero or negative.
     * @throws InsufficientFundsException If the withdrawal exceeds the available balance plus the overdraft limit.
     */
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

    /**
     * Returns a formatted string containing the current account's detailed information,
     * including its overdraft limit and interest rate.
     *
     * @return A string containing account details.
     */
    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nOverdraft Limit: " + overdraftLimit
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
