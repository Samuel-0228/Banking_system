package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

/**
 * Represents a savings account in the banking system.
 * Protects a minimum balance and earns interest on the deposited funds.
 */
public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;

    /**
     * Constructs a SavingsAccount with default interest rate and minimum balance.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param password      The password for accessing the account.
     */
    public SavingsAccount(String accountNumber, String customerId, String branch, double balance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, 0.05, 500.0, password);
    }

    /**
     * Constructs a SavingsAccount with an existing loan balance.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param loanBalance   The outstanding loan balance on the account.
     * @param password      The password for accessing the account.
     */
    public SavingsAccount(String accountNumber, String customerId, String branch, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, loanBalance, 0.05, 500.0, password);
    }

    /**
     * Constructs a SavingsAccount with custom interest rate and minimum balance.
     *
     * @param accountNumber  The unique identifier for the account.
     * @param customerId     The unique identifier for the customer.
     * @param customerName   The name of the customer.
     * @param branch         The branch where the account was opened.
     * @param balance        The initial balance of the account.
     * @param interestRate   The interest rate applicable to the account.
     * @param minimumBalance The minimum balance that must be maintained.
     * @param password       The password for accessing the account.
     */
    public SavingsAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double interestRate, double minimumBalance, String password) {
        this(accountNumber, customerId, customerName, branch, balance, 0.0, interestRate, minimumBalance, password);
    }

    /**
     * Constructs a SavingsAccount with all details including loan balance, custom interest rate, and minimum balance.
     *
     * @param accountNumber  The unique identifier for the account.
     * @param customerId     The unique identifier for the customer.
     * @param customerName   The name of the customer.
     * @param branch         The branch where the account was opened.
     * @param balance        The initial balance of the account.
     * @param loanBalance    The outstanding loan balance on the account.
     * @param interestRate   The interest rate applicable to the account.
     * @param minimumBalance The minimum balance that must be maintained.
     * @param password       The password for accessing the account.
     */
    public SavingsAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double loanBalance, double interestRate,
            double minimumBalance, String password) {
        super(accountNumber, customerId, customerName, branch, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }

    /**
     * Retrieves the specific type of the account.
     *
     * @return A string representing the account type ("Savings").
     */
    @Override
    public String getAccountType() {
        return "Savings";
    }

    /**
     * Retrieves the interest rate applicable to the savings account.
     *
     * @return The interest rate as a decimal.
     */
    @Override
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Retrieves the minimum balance required for this account.
     *
     * @return The minimum balance limit.
     */
    public double getMinimumBalance() {
        return minimumBalance;
    }

    /**
     * Deposits a specified amount into the account.
     *
     * @param amount The amount to deposit.
     * @throws InvalidAmountException If the amount is zero or negative.
     */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
    }

    /**
     * Withdraws a specified amount from the savings account.
     * Blocks the withdrawal if it drops the balance below the minimum required balance.
     *
     * @param amount The amount to withdraw.
     * @throws InvalidAmountException     If the amount is zero or negative.
     * @throws InsufficientFundsException If the withdrawal drops the balance below the minimum limit.
     */
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

    /**
     * Applies interest to the current balance.
     */
    public void applyInterest() {
        balance += balance * interestRate;
    }

    /**
     * Returns a formatted string containing the savings account's detailed information,
     * including minimum balance and interest rate.
     *
     * @return A string containing account details.
     */
    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nMinimum Balance: " + minimumBalance
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
