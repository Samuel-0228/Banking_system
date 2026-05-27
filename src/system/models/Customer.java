package system.models;

import java.sql.SQLException;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String email;

    public Customer(String customerId, String firstName, String lastName,
            String address, String phoneNumber, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

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

    public void openAccount(Bank bank, String type, double initialBalance, String password)
            throws SQLException {
        bank.openAccountForCustomer(this, type, initialBalance, password);
    }

    public void closeAccount(Bank bank, String accountNumber) throws SQLException {
        bank.closeAccount(accountNumber);
    }

    public List<Account> accessAccounts(Bank bank) throws SQLException {
        return bank.getAccountsForCustomer(customerId);
    }

    @Override
    public String toString() {
        return customerId + " - " + getFullName();
    }
}
