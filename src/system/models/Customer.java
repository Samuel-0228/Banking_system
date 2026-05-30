package system.models;

import java.sql.SQLException;
import java.util.List;

/**
 * Represents a customer of the bank.
 * Holds personal details and provides methods to interact with their accounts.
 */
public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;

    /**
     * Constructs a new Customer with full details.
     *
     * @param customerId  The unique identifier for the customer.
     * @param firstName   The customer's first name.
     * @param lastName    The customer's last name.
     * @param address     The customer's address.
     * @param phoneNumber The customer's phone number.
     * @param email       The customer's email address.
     */
    public Customer(String customerId, String firstName, String lastName,
            String address, String phoneNumber, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Constructs a new Customer without an address.
     *
     * @param customerId  The unique identifier for the customer.
     * @param firstName   The customer's first name.
     * @param lastName    The customer's last name.
     * @param phoneNumber The customer's phone number.
     * @param email       The customer's email address.
     */
    public Customer(String customerId, String firstName, String lastName,
            String phoneNumber, String email) {
        this(customerId, firstName, lastName, "", phoneNumber, email);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the customer's full name.
     *
     * @return The concatenated first and last name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Opens a new account for the customer.
     *
     * @param bank           The bank where the account will be opened.
     * @param type           The type of account (e.g., Savings, Current).
     * @param initialBalance The initial deposit amount.
     * @param password       The password for the new account.
     * @throws SQLException If a database access error occurs.
     */
    public void openAccount(Bank bank, String type, double initialBalance, String password)
            throws SQLException {
        bank.openAccountForCustomer(this, type, initialBalance, password, customerId);
    }

    /**
     * Closes an existing account for the customer.
     *
     * @param bank          The bank where the account is held.
     * @param accountNumber The account number to close.
     * @throws SQLException If a database access error occurs.
     */
    public void closeAccount(Bank bank, String accountNumber) throws SQLException {
        bank.closeAccount(accountNumber);
    }

    /**
     * Retrieves all accounts owned by this customer.
     *
     * @param bank The bank holding the accounts.
     * @return A list of the customer's accounts.
     * @throws SQLException If a database access error occurs.
     */
    public List<Account> accessAccounts(Bank bank) throws SQLException {
        return bank.getAccountsForCustomer(customerId);
    }

    @Override
    public String toString() {
        return customerId + " - " + getFullName();
    }
}
