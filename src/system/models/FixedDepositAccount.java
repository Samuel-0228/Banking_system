package system.models;

import system.exceptions.InsufficientFundsException;
import system.exceptions.InvalidAmountException;

import java.time.LocalDate;

/**
 * Represents a fixed deposit account in the banking system.
 * Funds are locked for a specific period and earn a fixed interest rate.
 */
public class FixedDepositAccount extends Account {
    private final double interestRate;
    private final int lockPeriodMonths;
    private LocalDate maturityDate;

    /**
     * Constructs a FixedDepositAccount with default interest rate and a 12-month lock period.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param password      The password for accessing the account.
     */
    public FixedDepositAccount(String accountNumber, String customerId, String branch, double balance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, 0.10, 12,
                LocalDate.now().plusMonths(12), password);
    }

    /**
     * Constructs a FixedDepositAccount with an existing loan balance.
     *
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance of the account.
     * @param loanBalance   The outstanding loan balance on the account.
     * @param password      The password for accessing the account.
     */
    public FixedDepositAccount(String accountNumber, String customerId, String branch, double balance,
            double loanBalance, String password) {
        this(accountNumber, customerId, customerId, branch, balance, loanBalance, 0.10, 12,
                LocalDate.now().plusMonths(12), password);
    }

    /**
     * Constructs a FixedDepositAccount with a custom interest rate and lock period.
     *
     * @param accountNumber    The unique identifier for the account.
     * @param customerId       The unique identifier for the customer.
     * @param customerName     The name of the customer.
     * @param branch           The branch where the account was opened.
     * @param balance          The initial balance of the account.
     * @param interestRate     The interest rate applicable to the account.
     * @param lockPeriodMonths The number of months the funds are locked.
     * @param maturityDate     The date when the fixed deposit matures.
     * @param password         The password for accessing the account.
     */
    public FixedDepositAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double interestRate, int lockPeriodMonths,
            LocalDate maturityDate, String password) {
        this(accountNumber, customerId, customerName, branch, balance, 0.0, interestRate,
                lockPeriodMonths, maturityDate, password);
    }

    /**
     * Constructs a FixedDepositAccount with all details including loan balance and custom lock period.
     *
     * @param accountNumber    The unique identifier for the account.
     * @param customerId       The unique identifier for the customer.
     * @param customerName     The name of the customer.
     * @param branch           The branch where the account was opened.
     * @param balance          The initial balance of the account.
     * @param loanBalance      The outstanding loan balance on the account.
     * @param interestRate     The interest rate applicable to the account.
     * @param lockPeriodMonths The number of months the funds are locked.
     * @param maturityDate     The date when the fixed deposit matures.
     * @param password         The password for accessing the account.
     */
    public FixedDepositAccount(String accountNumber, String customerId, String customerName, String branch,
            double balance, double loanBalance, double interestRate,
            int lockPeriodMonths, LocalDate maturityDate, String password) {
        super(accountNumber, customerId, customerName, branch, balance, loanBalance, password);
        this.interestRate = interestRate;
        this.lockPeriodMonths = lockPeriodMonths;
        this.maturityDate = (maturityDate == null)
                ? LocalDate.now().plusMonths(lockPeriodMonths)
                : maturityDate;
    }

    /**
     * Retrieves the specific type of the account.
     *
     * @return A string representing the account type ("Fixed Deposit").
     */
    @Override
    public String getAccountType() {
        return "Fixed Deposit";
    }

    /**
     * Retrieves the interest rate applicable to the fixed deposit account.
     *
     * @return The interest rate as a decimal.
     */
    @Override
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Retrieves the duration for which funds are locked.
     *
     * @return The lock period in months.
     */
    public int getLockPeriodMonths() {
        return lockPeriodMonths;
    }

    /**
     * Retrieves the date on which the fixed deposit matures.
     *
     * @return The maturity date.
     */
    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    /**
     * Deposits a specified amount into the account.
     * For fixed deposits, any additional deposit (top-up) renews the lock period.
     *
     * @param amount The amount to deposit.
     * @throws InvalidAmountException If the amount is zero or negative.
     */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        super.deposit(amount);
        maturityDate = LocalDate.now().plusMonths(lockPeriodMonths);
    }

    /**
     * Withdraws a specified amount from the account.
     * Withdrawals are completely blocked until the maturity date is reached.
     *
     * @param amount The amount to withdraw.
     * @throws InvalidAmountException     If the amount is zero or negative.
     * @throws InsufficientFundsException If the account is locked, or if the balance is less than the withdrawal amount.
     */
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

    /**
     * Returns a formatted string containing the fixed deposit account's detailed information,
     * including lock period, maturity date, and interest rate.
     *
     * @return A string containing account details.
     */
    @Override
    public String displayAccountInfo() {
        return super.displayAccountInfo()
                + "\nLock Period (months): " + lockPeriodMonths
                + "\nMaturity Date: " + maturityDate
                + "\nInterest Rate: " + (interestRate * 100) + "%";
    }
}
