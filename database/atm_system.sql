
DROP DATABASE IF EXISTS atm_system;
CREATE DATABASE atm_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE atm_system;

-- ---------------------------------------------------------------------------
-- Customers table
-- ---------------------------------------------------------------------------
CREATE TABLE customers (
    customer_id   VARCHAR(10)  PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------
-- Accounts table  (acts as the Login table — password is stored here so
-- login is account_number + password)
-- ---------------------------------------------------------------------------
CREATE TABLE accounts (
    account_number  VARCHAR(15)    PRIMARY KEY,
    customer_id     VARCHAR(10)    NOT NULL,
    account_type    VARCHAR(20)    NOT NULL,
    balance         DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    loan_balance    DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    password        VARCHAR(255)   NOT NULL,
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------------
-- Transactions table
-- ---------------------------------------------------------------------------
CREATE TABLE transactions (
    transaction_id    INT            PRIMARY KEY AUTO_INCREMENT,
    account_number    VARCHAR(15)    NOT NULL,
    transaction_type  VARCHAR(20)    NOT NULL,
    amount            DECIMAL(15, 2) NOT NULL,
    balance_after     DECIMAL(15, 2) NOT NULL,
    target_account    VARCHAR(15)    NULL,
    transaction_date  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_account
        FOREIGN KEY (account_number) REFERENCES accounts(account_number)
        ON DELETE CASCADE
);

CREATE INDEX idx_tx_account_date ON transactions(account_number, transaction_date DESC);
