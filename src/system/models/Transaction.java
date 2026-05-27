package system.models;

import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private String targetAccount;
    private Timestamp transactionDate;

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

    public Transaction(String accountNumber, String transactionType, double amount,
            double balanceAfter, String targetAccount) {
        this(0, accountNumber, transactionType, amount, balanceAfter, targetAccount, null);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
}
