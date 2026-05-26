# ATM Banking System
#  ATM is the name  we gave it for the bank
A Java Swing ATM/banking application backed by MySQL via JDBC. Customers can register, create accounts, deposit, withdraw, transfer money between accounts, and view full transaction history.

---

## 1. Technologies

- **Java** 
- **NetBeans IDE** 
- **Java Swing** for all GUI 
- **JDBC** (`com.mysql.cj.jdbc.Driver`) for database access
- **MySQL** running under **XAMPP** (localhost)

---

## 2. Project Layout

```
ATMSystem/
├── src/atmsystem/
│   ├── main/
│   │   └── Main.java                 ← application entry point
│   ├── models/
│   │   ├── Bank.java                 ← factory for account subtypes
│   │   ├── Customer.java
│   │   ├── Account.java              ← abstract base (encapsulation, abstraction)
│   │   ├── SavingsAccount.java       ← inheritance
│   │   ├── CurrentAccount.java       ← polymorphism: overrides withdraw() with overdraft
│   │   ├── FixedDepositAccount.java
│   │   └── Transaction.java
│   ├── operations/
│   │   ├── Deposit.java
│   │   ├── Withdraw.java
│   │   └── Transfer.java
│   ├── database/
│   │   ├── DatabaseHandler.java      ← all JDBC CRUD
│   │   └── FileHandler.java          ← transactions.log
│   ├── exceptions/
│   │   ├── InsufficientFundsException.java
│   │   ├── InvalidAmountException.java
│   │   ├── AccountNotFoundException.java
│   │   └── InvalidLoginException.java
│   └── gui/
│       ├── Theme.java                ← shared colors / fonts / widgets
│       ├── LoginGUI.java
│       ├── SignUpGUI.java            ← customer registration
│       ├── AccountCreationGUI.java
│       ├── DashboardGUI.java
│       ├── DepositGUI.java
│       ├── WithdrawGUI.java
│       ├── TransferGUI.java
│       └── TransactionHistoryGUI.java
├── database/
│   └── atm_system.sql                ← MySQL schema
├── lib/
│   └── (place mysql-connector-j-x.x.x.jar here)
├── nbproject/                        ← NetBeans Ant project config
├── build.xml
├── manifest.mf
└── README.md
```

---


### 3.1 Create the database
-  Open phpMyAdmin at `http://localhost/phpmyadmin/`.
- then  Choose **Import** → select `database/atm_system.sql` → **Go**.




### 3.3 Add the MySQL JDBC driver
1.  **MySQL Connector/J** (Platform Independent) from:
   https://dev.mysql.com/downloads/connector/j/
2. Extract the `.jar` (mysql-connector-j-9.7.0)
3.
   ```
   file.reference.mysql-connector=lib/-mysql-connector-j-9.7.0.jar
   ```


### 3.4 Run

In NetBeans: **Open Project** → select the `ATMSystem` folder → **Run** 
If MySQL isn't running or the driver isn't on the classpath, the app shows a clear error dialog at startup.



## 4. System Flow (detailed)

1. **Application starts** → `Main.java` tests the DB connection, then opens `LoginGUI`.
2. **LoginGUI** offers three actions:
   - **SIGN IN** — authenticates an existing account number + password.
   - **CLEAR** — clears the form.
   - **SIGN UP** — starts new-customer registration.
3. **SignUpGUI (Customer Registration)** — opens with:
   - `Customer ID` auto-generated in the format `C0001`, `C0002`, … (read-only).
   - First name, last name, phone number, email (all required; email + phone validated).
   - On **NEXT**, the customer is saved and `AccountCreationGUI` opens.
4. **AccountCreationGUI** — opens with:
   - `Account Number` auto-generated as `ACC-0001`, `ACC-0002`, … (read-only).
   - Account type dropdown: **Savings**, **Current**, **Fixed Deposit**.
   - Initial balance (non-negative number).
   - Password + confirm password (≥ 4 characters).
   - On **CREATE ACCOUNT**, a success message is shown and control returns to `LoginGUI`.
5. **Login** with account number + password → `DashboardGUI` opens.
6. **DashboardGUI** shows the welcome banner, current balance, and six buttons:
   - **Check Balance** — popup with current balance and account type.
   - **Deposit Money** → `DepositGUI`.
   - **Withdraw Money** → `WithdrawGUI`.
   - **Transfer Money** → `TransferGUI`.
   - **Transaction History** → `TransactionHistoryGUI` (table view of all entries).
   - **Logout** → returns to `LoginGUI`.

Every successful deposit/withdraw/transfer:
- Updates `accounts.balance` in MySQL.
- Inserts a row into `transactions` (DB).
- Appends a line to `transactions.log` (file).

---

## 5. Database Design

### `customers`
| Column        | Type         | Notes                          |
|---------------|--------------|--------------------------------|
| customer_id   | VARCHAR(10)  | PRIMARY KEY, format `C####`    |
| first_name    | VARCHAR(50)  | NOT NULL                       |
| last_name     | VARCHAR(50)  | NOT NULL                       |
| phone_number  | VARCHAR(20)  | NOT NULL                       |
| email         | VARCHAR(100) | NOT NULL                       |
| created_at    | TIMESTAMP    | default `CURRENT_TIMESTAMP`    |

### `accounts` (doubles as the login table)
| Column          | Type           | Notes                                   |
|-----------------|----------------|-----------------------------------------|
| account_number  | VARCHAR(15)    | PRIMARY KEY, format `ACC-####`          |
| customer_id     | VARCHAR(10)    | FK → `customers.customer_id`            |
| account_type    | VARCHAR(20)    | `Savings`, `Current`, or `Fixed Deposit`|
| balance         | DECIMAL(15,2)  | NOT NULL, default 0.00                  |
| password        | VARCHAR(255)   | NOT NULL                                |
| created_at      | TIMESTAMP      | default `CURRENT_TIMESTAMP`             |

Login uses `account_number` + `password` from this table (per requirement: "log in using Account Number / Password").

### `transactions`
| Column            | Type           | Notes                                   |
|-------------------|----------------|-----------------------------------------|
| transaction_id    | INT            | PRIMARY KEY, AUTO_INCREMENT             |
| account_number    | VARCHAR(15)    | FK → `accounts.account_number`          |
| transaction_type  | VARCHAR(20)    | `DEPOSIT`, `WITHDRAW`, `TRANSFER_OUT`, `TRANSFER_IN` |
| amount            | DECIMAL(15,2)  | NOT NULL                                |
| balance_after     | DECIMAL(15,2)  | NOT NULL — snapshot for history view    |
| target_account    | VARCHAR(15)    | nullable — set for transfers            |
| transaction_date  | TIMESTAMP      | default `CURRENT_TIMESTAMP`             |

A composite index `(account_number, transaction_date DESC)` is created to make per-account history queries fast.

---

## 6. OOP Concepts Used

- **Classes & Objects** — every domain entity has its own class.
- **Encapsulation** — `Account` fields are `protected`; access is via getters/setters.
- **Inheritance** — `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` extend `Account`.
- **Polymorphism** — `CurrentAccount.withdraw()` overrides the base behavior to allow overdraft up to $500. `getAccountType()` and `getInterestRate()` are polymorphic.
- **Abstraction** — `Account` is abstract; clients depend on the abstract type, not concrete subclasses.
- **Exception Handling** — domain-specific checked exceptions for validation/business errors.

---

## 7. Exception Handling

| Exception                        | Thrown when…                                            | Caught in                              |
|----------------------------------|---------------------------------------------------------|----------------------------------------|
| `InvalidLoginException`          | Empty fields or wrong account/password                  | `LoginGUI.doLogin()`                    |
| `InvalidAmountException`         | Amount ≤ 0 or non-numeric                               | Deposit/Withdraw/Transfer GUIs          |
| `InsufficientFundsException`     | Withdraw/transfer exceeds available balance (+overdraft for Current) | Withdraw/Transfer GUIs |
| `AccountNotFoundException`       | Transfer target account doesn't exist                   | `TransferGUI`                           |
| `SQLException`                   | Any DB error (connection, query, constraint)            | All GUIs (shown in an error dialog)     |
| `NumberFormatException`          | User typed a non-number in an amount field              | Deposit/Withdraw/Transfer GUIs          |

All caught exceptions surface to the user as a `JOptionPane` dialog with a clear message — the app never silently swallows an error.

---

## 8. Validation Rules

| Field                  | Rule                                                  |
|------------------------|-------------------------------------------------------|
| First/last name        | Non-empty                                             |
| Phone number           | 7–15 digits, optional leading `+`, dashes/spaces ok   |
| Email                  | Matches `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$` |
| Password               | ≥ 4 characters; confirm-password must match           |
| Initial balance        | Non-negative number                                   |
| Deposit / withdraw     | Numeric, > 0                                          |
| Transfer receiver      | Non-empty, must exist in `accounts`, cannot be self   |
| Withdraw / transfer    | Amount ≤ balance (Current allows up to $500 overdraft)|

---

## 9. Functional Requirements

- FR-1 — System auto-generates `customer_id` as `C####` and `account_number` as `ACC-####`.
- FR-2 — Customer registration captures first name, last name, phone, email.
- FR-3 — Account creation captures account type, initial balance, and password.
- FR-4 — Customer can log in with account number + password.
- FR-5 — Dashboard exposes check balance, deposit, withdraw, transfer, history, logout.
- FR-6 — Deposit increases balance and records a `DEPOSIT` transaction.
- FR-7 — Withdraw decreases balance only if sufficient funds; records `WITHDRAW`.
- FR-8 — Transfer verifies target account exists and sender has funds; records both `TRANSFER_OUT` (sender) and `TRANSFER_IN` (receiver).
- FR-9 — Transaction history shows ID, type, amount, balance after, target account, date/time.
- FR-10 — All persisted state is in MySQL; a parallel flat-file log (`transactions.log`) is kept for audit.

## 10. Non-Functional Requirements

- **Usability** — single consistent visual theme (`Theme.java`) across every screen.
- **Maintainability** — one class per file, packages by responsibility, no cross-cutting "god classes".
- **Reliability** — every DB call is wrapped in try-with-resources; failed calls surface a clear dialog.
- **Portability** — pure Java 8; runs on Windows / macOS / Linux with the same project.


---


"# Banking_system" 
