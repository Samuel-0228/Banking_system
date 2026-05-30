package system.models;

import java.sql.Timestamp;

/**
 * Represents a single transaction event on an account.
 * Tracks the nature, amount, and resulting balance of the transaction.
 */
public class Transaction {
    private int transactionId;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private String targetAccount;
    private Timestamp transactionDate;

    /**
     * Constructs a Transaction with full details, typically retrieved from the database.
     *
     * @param transactionId   The unique identifier for the transaction.
     * @param accountNumber   The primary account involved in the transaction.
     * @param transactionType The type of transaction (e.g., DEPOSIT, WITHDRAW, TRANSFER).
     * @param amount          The amount of the transaction.
     * @param balanceAfter    The account balance immediately following this transaction.
     * @param targetAccount   The secondary account involved (e.g., in a transfer). Can be null.
     * @param transactionDate The timestamp when the transaction occurred.
     */
    public Transaction(int transactionId, String accountNumber, String transactionType,
            double amount, double balanceAfter, String targetAccount,
            Timestamp transactionDate) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.targetAccount = targetAccount;
        this.transactionDate = transactionDate;
    }

    /**
     * Constructs a new Transaction before it is assigned an ID or timestamp by the database.
     *
     * @param accountNumber   The primary account involved in the transaction.
     * @param transactionType The type of transaction.
     * @param amount          The amount of the transaction.
     * @param balanceAfter    The account balance immediately following this transaction.
     * @param targetAccount   The secondary account involved. Can be null.
     */
    public Transaction(String accountNumber, String transactionType, double amount,
            double balanceAfter, String targetAccount) {
        this(0, accountNumber, transactionType, amount, balanceAfter, targetAccount, null);
    }

    /**
     * Retrieves the transaction's unique ID.
     *
     * @return The transaction ID.
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Retrieves the account number associated with this transaction.
     *
     * @return The account number.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Retrieves the type of the transaction.
     *
     * @return The transaction type (e.g., DEPOSIT).
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Retrieves the amount involved in the transaction.
     *
     * @return The transaction amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Retrieves the balance of the account after the transaction was applied.
     *
     * @return The post-transaction balance.
     */
    public double getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * Retrieves the target account involved in the transaction, if any.
     *
     * @return The target account number, or null if not applicable.
     */
    public String getTargetAccount() {
        return targetAccount;
    }

    /**
     * Retrieves the date and time when the transaction occurred.
     *
     * @return The transaction timestamp.
     */
    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transaction ID. Typically used after database insertion.
     *
     * @param transactionId The newly generated ID.
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Sets the transaction date. Typically used when retrieving from the database.
     *
     * @param transactionDate The timestamp of the transaction.
     */
    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
}
