package system.models;

import system.database.DatabaseHandler;
import system.exceptions.AccountNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private final String bankName;
    private final String bankAddress;
    private final String bankCode;
    private final List<String> branches;
    private final List<Customer> customers;
    private final List<Account> accounts;

    public Bank(String bankName, String bankCode) {
        this(bankName, "Head Office", bankCode);
    }

    public Bank(String bankName, String bankAddress, String bankCode) {
        this.bankName = bankName;
        this.bankAddress = bankAddress;
        this.bankCode = bankCode;
        this.branches = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public String getBankCode() {
        return bankCode;
    }

    public List<String> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addBranch(String branchName) {
        if (branchName != null) {
            String trimmed = branchName.trim();
            if (!trimmed.isEmpty() && !branches.contains(trimmed)) {
                branches.add(trimmed);
            }
        }
    }

    public void addCustomer(Customer customer) throws SQLException {
        DatabaseHandler.saveCustomer(customer);
        refreshCustomers();
    }

    public void updateCustomer(Customer customer) throws SQLException {
        DatabaseHandler.updateCustomer(customer);
        refreshCustomers();
    }

    public void removeCustomer(String customerId) throws SQLException {
        DatabaseHandler.deleteCustomer(customerId);
        refreshData();
    }

    public Customer searchCustomerById(String customerId) throws SQLException {
        return DatabaseHandler.getCustomer(customerId);
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        return DatabaseHandler.searchCustomers(keyword);
    }

    public void addAccount(Account account) throws SQLException {
        DatabaseHandler.saveAccount(account);
        refreshAccounts();
    }

    public void updateAccount(Account account) throws SQLException {
        DatabaseHandler.updateAccount(account);
        refreshAccounts();
    }

    public void removeAccount(String accountNumber) throws SQLException {
        DatabaseHandler.deleteAccount(accountNumber);
        refreshAccounts();
    }

    public void closeAccount(String accountNumber) throws SQLException {
        removeAccount(accountNumber);
    }

    public Account searchAccount(String accountNumber) throws SQLException, AccountNotFoundException {
        return DatabaseHandler.getAccount(accountNumber);
    }

    public List<Account> searchAccounts(String keyword) throws SQLException {
        return DatabaseHandler.searchAccounts(keyword);
    }

    public List<Account> getAccountsForCustomer(String customerId) throws SQLException {
        return DatabaseHandler.getAccountsForCustomer(customerId);
    }

    public Account openAccountForCustomer(Customer customer, String type,
            double initialBalance, String password) throws SQLException {
        String accountNumber = DatabaseHandler.generateAccountNumber();
        Account account = createAccount(type, accountNumber, customer.getCustomerId(),
                initialBalance, password);
        addAccount(account);
        return account;
    }

    public void refreshData() throws SQLException {
        refreshCustomers();
        refreshAccounts();
    }

    public void refreshCustomers() throws SQLException {
        customers.clear();
        customers.addAll(DatabaseHandler.getAllCustomers());
    }

    public void refreshAccounts() throws SQLException {
        accounts.clear();
        accounts.addAll(DatabaseHandler.getAllAccounts());
    }

    public static Account createAccount(String type, String accountNumber,
            String customerId, double balance, String password) {
        return createAccount(type, accountNumber, customerId, balance, 0.0, password);
    }

    public static Account createAccount(String type, String accountNumber,
            String customerId, double balance,
            double loanBalance, String password) {
        switch (type.toLowerCase()) {
            case "savings":
                return new SavingsAccount(accountNumber, customerId, balance, loanBalance, password);
            case "current":
                return new CurrentAccount(accountNumber, customerId, balance, loanBalance, password);
            case "fixed":
            case "fixed deposit":
                return new FixedDepositAccount(accountNumber, customerId, balance, loanBalance, password);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
