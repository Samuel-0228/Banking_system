package system.database;

import system.exceptions.AccountNotFoundException;
import system.models.Account;
import system.models.Bank;
import system.models.Customer;
import system.models.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the banking system.
 * Manages connections and executes CRUD operations for customers, accounts, and transactions.
 */
public class DatabaseHandler {

    private static final String URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Establishes a connection to the MySQL database.
     *
     * @return A valid Connection object.
     * @throws SQLException If the connection fails or the driver is missing.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j-x.x.x.jar to project libraries.",
                    e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Tests the database connection.
     *
     * @return True if the connection is successful, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Generates a new unique customer ID (e.g., C0001, C0002).
     *
     * @return A newly generated customer ID.
     * @throws SQLException If a database error occurs.
     */
    public static String generateCustomerId() throws SQLException {
        String sql = "SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            int next = 1;
            if (rs.next()) {
                String last = rs.getString("customer_id");
                next = Integer.parseInt(last.substring(1)) + 1;
            }
            return String.format("C%04d", next);
        }
    }

    /**
     * Generates a new unique account number (e.g., ACC-0001, ACC-0002).
     *
     * @return A newly generated account number.
     * @throws SQLException If a database error occurs.
     */
    public static String generateAccountNumber() throws SQLException {
        String sql = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            int next = 1;
            if (rs.next()) {
                String last = rs.getString("account_number");
                next = Integer.parseInt(last.substring(4)) + 1;
            }
            return String.format("ACC-%04d", next);
        }
    }

    /**
     * Inserts a new customer into the database.
     *
     * @param customer The customer object to save.
     * @throws SQLException If a database error occurs.
     */
    public static void saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, address, phone_number, email) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerId());
            ps.setString(2, customer.getFirstName());
            ps.setString(3, customer.getLastName());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getPhoneNumber());
            ps.setString(6, customer.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing customer's details in the database.
     *
     * @param customer The customer object with updated details.
     * @throws SQLException If a database error occurs.
     */
    public static void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, address = ?, phone_number = ?, email = ? "
                + "WHERE customer_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhoneNumber());
            ps.setString(5, customer.getEmail());
            ps.setString(6, customer.getCustomerId());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a customer from the database by their ID.
     *
     * @param customerId The ID of the customer to delete.
     * @throws SQLException If a database error occurs.
     */
    public static void deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves a customer from the database by their ID.
     *
     * @param customerId The ID to search for.
     * @return The Customer object if found, or null.
     * @throws SQLException If a database error occurs.
     */
    public static Customer getCustomer(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all customers stored in the database.
     *
     * @return A list of all customers.
     * @throws SQLException If a database error occurs.
     */
    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        }
        return customers;
    }

    /**
     * Searches for customers matching a specific keyword.
     *
     * @param keyword The keyword to search for (matches ID, name, or email).
     * @return A list of matching customers.
     * @throws SQLException If a database error occurs.
     */
    public static List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_id LIKE ? OR first_name LIKE ? "
                + "OR last_name LIKE ? OR email LIKE ? ORDER BY customer_id";
        String pattern = "%" + keyword + "%";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * Inserts a new account into the database.
     *
     * @param account The account object to save.
     * @throws SQLException If a database error occurs.
     */
    public static void saveAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, branch_name, balance, loan_balance, password) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getCustomerId());
            ps.setString(3, account.getAccountType());
            ps.setString(4, account.getBranch());
            ps.setDouble(5, account.getBalance());
            ps.setDouble(6, account.getLoanBalance());
            ps.setString(7, account.getPassword());
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing account's details in the database.
     *
     * @param account The account object with updated details.
     * @throws SQLException If a database error occurs.
     */
    public static void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET account_type = ?, branch_name = ?, balance = ?, loan_balance = ?, password = ? "
                + "WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountType());
            ps.setString(2, account.getBranch());
            ps.setDouble(3, account.getBalance());
            ps.setDouble(4, account.getLoanBalance());
            ps.setString(5, account.getPassword());
            ps.setString(6, account.getAccountNumber());
            ps.executeUpdate();
        }
    }

    /**
     * Deletes an account from the database by its account number.
     *
     * @param accountNumber The account number to delete.
     * @throws SQLException If a database error occurs.
     */
    public static void deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves an account from the database by its account number.
     *
     * @param accountNumber The account number to search for.
     * @return The Account object.
     * @throws SQLException             If a database error occurs.
     * @throws AccountNotFoundException If the account is not found.
     */
    public static Account getAccount(String accountNumber) throws SQLException, AccountNotFoundException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        throw new AccountNotFoundException(accountNumber);
    }

    /**
     * Retrieves all accounts stored in the database.
     *
     * @return A list of all accounts.
     * @throws SQLException If a database error occurs.
     */
    public static List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        }
        return accounts;
    }

    /**
     * Retrieves all accounts associated with a specific customer.
     *
     * @param customerId The customer ID to filter by.
     * @return A list of accounts belonging to the customer.
     * @throws SQLException If a database error occurs.
     */
    public static List<Account> getAccountsForCustomer(String customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapAccount(rs));
                }
            }
        }
        return accounts;
    }

    /**
     * Searches for accounts matching a specific keyword.
     *
     * @param keyword The keyword to search for (matches account number, customer ID, or account type).
     * @return A list of matching accounts.
     * @throws SQLException If a database error occurs.
     */
    public static List<Account> searchAccounts(String keyword) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE account_number LIKE ? OR customer_id LIKE ? "
                + "OR account_type LIKE ? ORDER BY account_number";
        String pattern = "%" + keyword + "%";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapAccount(rs));
                }
            }
        }
        return accounts;
    }

    /**
     * Checks if an account exists in the database.
     *
     * @param accountNumber The account number to check.
     * @return True if the account exists, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static boolean accountExists(String accountNumber) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Updates only the balance of a specific account.
     *
     * @param accountNumber The account number to update.
     * @param newBalance    The new balance.
     * @throws SQLException If a database error occurs.
     */
    public static void updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    /**
     * Updates only the loan balance of a specific account.
     *
     * @param accountNumber The account number to update.
     * @param loanBalance   The new loan balance.
     * @throws SQLException If a database error occurs.
     */
    public static void updateLoanBalance(String accountNumber, double loanBalance) throws SQLException {
        String sql = "UPDATE accounts SET loan_balance = ? WHERE account_number = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, loanBalance);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    /**
     * Records a new transaction into the database.
     *
     * @param t The transaction object containing the details.
     * @throws SQLException If a database error occurs.
     */
    public static void recordTransaction(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, balance_after, target_account) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getAccountNumber());
            ps.setString(2, t.getTransactionType());
            ps.setDouble(3, t.getAmount());
            ps.setDouble(4, t.getBalanceAfter());
            if (t.getTargetAccount() != null) {
                ps.setString(5, t.getTargetAccount());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setTransactionId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves all transactions associated with a specific account.
     *
     * @param accountNumber The account number to query.
     * @return A list of transactions ordered by date descending.
     * @throws SQLException If a database error occurs.
     */
    public static List<Transaction> getTransactions(String accountNumber) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("transaction_date");
                    list.add(new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getString("account_number"),
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getDouble("balance_after"),
                            rs.getString("target_account"),
                            ts));
                }
            }
        }
        return list;
    }

    /**
     * Authenticates an account using its account number and password.
     *
     * @param accountNumber The account number to authenticate.
     * @param password      The password to verify.
     * @return The authenticated Account object, or null if credentials are invalid.
     * @throws SQLException If a database error occurs.
     */
    public static Account authenticate(String accountNumber, String password) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND password = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    private static Customer mapCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getString("email"));
    }

    private static Account mapAccount(ResultSet rs) throws SQLException {
        return Bank.createAccount(
                rs.getString("account_type"),
                rs.getString("account_number"),
                rs.getString("customer_id"),
                rs.getString("branch_name"),
                rs.getDouble("balance"),
                rs.getDouble("loan_balance"),
                rs.getString("password"));
    }
}
