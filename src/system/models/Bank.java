package system.models;

import system.database.DatabaseHandler;
import system.exceptions.AccountNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the main banking entity that manages customers, accounts, and branches.
 * Acts as a central service layer between the database and the rest of the application.
 */
public class Bank {
    private final String bankName;
    private final String bankAddress;
    private final String bankCode;
    private final List<String> branches;
    private final List<Customer> customers;
    private final List<Account> accounts;

    /**
     * Constructs a Bank with a default address ("Head Office").
     *
     * @param bankName The name of the bank.
     * @param bankCode The unique code for the bank.
     */
    public Bank(String bankName, String bankCode) {
        this(bankName, "Head Office", bankCode);
    }

    /**
     * Constructs a Bank with full details.
     *
     * @param bankName    The name of the bank.
     * @param bankAddress The physical address of the bank.
     * @param bankCode    The unique code for the bank.
     */
    public Bank(String bankName, String bankAddress, String bankCode) {
        this.bankName = bankName;
        this.bankAddress = bankAddress;
        this.bankCode = bankCode;
        this.branches = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    /**
     * Retrieves the bank's name.
     *
     * @return The bank name.
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Retrieves the bank's physical address.
     *
     * @return The bank address.
     */
    public String getBankAddress() {
        return bankAddress;
    }

    /**
     * Retrieves the bank's unique code.
     *
     * @return The bank code.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Retrieves an unmodifiable list of the bank's branches.
     *
     * @return The list of branches.
     */
    public List<String> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    /**
     * Retrieves an unmodifiable list of the bank's loaded customers.
     *
     * @return The list of customers.
     */
    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    /**
     * Retrieves an unmodifiable list of the bank's loaded accounts.
     *
     * @return The list of accounts.
     */
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Adds a new branch to the bank if it doesn't already exist.
     *
     * @param branchName The name of the branch to add.
     */
    public void addBranch(String branchName) {
        if (branchName != null) {
            String trimmed = branchName.trim();
            if (!trimmed.isEmpty() && !branches.contains(trimmed)) {
                branches.add(trimmed);
            }
        }
    }

    /**
     * Registers a new customer into the database and refreshes the local list.
     *
     * @param customer The customer to add.
     * @throws SQLException If a database error occurs.
     */
    public void addCustomer(Customer customer) throws SQLException {
        DatabaseHandler.saveCustomer(customer);
        refreshCustomers();
    }

    /**
     * Updates an existing customer's details in the database and refreshes the local list.
     *
     * @param customer The customer with updated details.
     * @throws SQLException If a database error occurs.
     */
    public void updateCustomer(Customer customer) throws SQLException {
        DatabaseHandler.updateCustomer(customer);
        refreshCustomers();
    }

    /**
     * Removes a customer from the database by ID and refreshes all data.
     *
     * @param customerId The unique identifier of the customer to remove.
     * @throws SQLException If a database error occurs.
     */
    public void removeCustomer(String customerId) throws SQLException {
        DatabaseHandler.deleteCustomer(customerId);
        refreshData();
    }

    /**
     * Searches for a customer by their unique ID.
     *
     * @param customerId The ID to search for.
     * @return The matching Customer, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public Customer searchCustomerById(String customerId) throws SQLException {
        return DatabaseHandler.getCustomer(customerId);
    }

    /**
     * Searches for customers by a keyword (e.g., name, ID, email).
     *
     * @param keyword The keyword to search for.
     * @return A list of matching customers.
     * @throws SQLException If a database error occurs.
     */
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        return DatabaseHandler.searchCustomers(keyword);
    }

    /**
     * Registers a new account into the database and refreshes the local list.
     *
     * @param account The account to add.
     * @throws SQLException If a database error occurs.
     */
    public void addAccount(Account account) throws SQLException {
        DatabaseHandler.saveAccount(account);
        refreshAccounts();
    }

    /**
     * Updates an existing account's details in the database and refreshes the local list.
     *
     * @param account The account with updated details.
     * @throws SQLException If a database error occurs.
     */
    public void updateAccount(Account account) throws SQLException {
        DatabaseHandler.updateAccount(account);
        refreshAccounts();
    }

    /**
     * Removes an account from the database by its number and refreshes all data.
     *
     * @param accountNumber The unique identifier of the account to remove.
     * @throws SQLException If a database error occurs.
     */
    public void removeAccount(String accountNumber) throws SQLException {
        DatabaseHandler.deleteAccount(accountNumber);
        refreshAccounts();
    }

    /**
     * Closes an account by calling the remove method.
     *
     * @param accountNumber The account number to close.
     * @throws SQLException If a database error occurs.
     */
    public void closeAccount(String accountNumber) throws SQLException {
        removeAccount(accountNumber);
    }

    /**
     * Searches for an account by its unique account number.
     *
     * @param accountNumber The account number to search for.
     * @return The matching Account.
     * @throws SQLException             If a database error occurs.
     * @throws AccountNotFoundException If the account is not found.
     */
    public Account searchAccount(String accountNumber) throws SQLException, AccountNotFoundException {
        return DatabaseHandler.getAccount(accountNumber);
    }

    /**
     * Searches for accounts by a keyword.
     *
     * @param keyword The keyword to search for.
     * @return A list of matching accounts.
     * @throws SQLException If a database error occurs.
     */
    public List<Account> searchAccounts(String keyword) throws SQLException {
        return DatabaseHandler.searchAccounts(keyword);
    }

    /**
     * Retrieves all accounts associated with a specific customer ID.
     *
     * @param customerId The ID of the customer.
     * @return A list of the customer's accounts.
     * @throws SQLException If a database error occurs.
     */
    public List<Account> getAccountsForCustomer(String customerId) throws SQLException {
        return DatabaseHandler.getAccountsForCustomer(customerId);
    }

    /**
     * Opens a new account for a customer, assigns an account number, and saves it.
     *
     * @param customer       The customer opening the account.
     * @param type           The type of account (e.g., Savings, Current).
     * @param initialBalance The initial deposit amount.
     * @param branch         The branch where the account is opened.
     * @param password       The password for the account.
     * @return The newly created account.
     * @throws SQLException If a database error occurs.
     */
    public Account openAccountForCustomer(Customer customer, String type,
            double initialBalance, String branch, String password) throws SQLException {
        String accountNumber = DatabaseHandler.generateAccountNumber();
        Account account = createAccount(type, accountNumber, customer.getCustomerId(),
                branch, initialBalance, password);
        addAccount(account);
        return account;
    }

    /**
     * Refreshes both customer and account lists from the database.
     *
     * @throws SQLException If a database error occurs.
     */
    public void refreshData() throws SQLException {
        refreshCustomers();
        refreshAccounts();
    }

    /**
     * Reloads the list of customers from the database.
     *
     * @throws SQLException If a database error occurs.
     */
    public void refreshCustomers() throws SQLException {
        customers.clear();
        customers.addAll(DatabaseHandler.getAllCustomers());
    }

    /**
     * Reloads the list of accounts from the database.
     *
     * @throws SQLException If a database error occurs.
     */
    public void refreshAccounts() throws SQLException {
        accounts.clear();
        accounts.addAll(DatabaseHandler.getAllAccounts());
    }

    /**
     * Factory method to create an Account instance without a loan balance.
     *
     * @param type          The account type.
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance.
     * @param password      The password.
     * @return The constructed Account object.
     */
    public static Account createAccount(String type, String accountNumber,
            String customerId, String branch, double balance, String password) {
        return createAccount(type, accountNumber, customerId, branch, balance, 0.0, password);
    }

    /**
     * Factory method to create an Account instance with all details including loan balance.
     *
     * @param type          The account type.
     * @param accountNumber The unique identifier for the account.
     * @param customerId    The unique identifier for the customer.
     * @param branch        The branch where the account was opened.
     * @param balance       The initial balance.
     * @param loanBalance   The outstanding loan balance.
     * @param password      The password.
     * @return The constructed Account object.
     * @throws IllegalArgumentException If an unknown account type is provided.
     */
    public static Account createAccount(String type, String accountNumber,
            String customerId, String branch, double balance,
            double loanBalance, String password) {
        switch (type.toLowerCase()) {
            case "savings":
                return new SavingsAccount(accountNumber, customerId, branch, balance, loanBalance, password);
            case "current":
                return new CurrentAccount(accountNumber, customerId, branch, balance, loanBalance, password);
            case "fixed":
            case "fixed deposit":
                return new FixedDepositAccount(accountNumber, customerId, branch, balance, loanBalance, password);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
