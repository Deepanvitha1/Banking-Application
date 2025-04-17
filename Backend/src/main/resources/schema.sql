show databases;
create database dbpproject;

use dbpproject;

CREATE TABLE user (
                      uid BIGINT auto_increment PRIMARY KEY,
                      first_name VARCHAR(50) NOT NULL,
                      last_name VARCHAR(50) NOT NULL,
                      email varchar(50),
                      phone_number VARCHAR(15) NOT NULL,
                      aadhar VARCHAR(12) UNIQUE NOT NULL,
                      address VARCHAR(255) NOT NULL,
                      dob DATE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      re_password VARCHAR(100) NOT NULL,
                      created_ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_ts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE accounts (
                          account_id VARCHAR(255) PRIMARY KEY,   -- Unique identifier for the account
                          user_id VARCHAR(255) NOT NULL,         -- User ID associated with the account
                          name VARCHAR(255) NOT NULL,            -- Name of the account holder
                          balance DECIMAL(15, 2) NOT NULL,       -- Account balance with 2 decimal precision
                          type VARCHAR(50) NOT NULL,             -- Type of account (e.g., Checking, Savings)
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Date and time the account was created
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Timestamp for when the record was last updated
);

CREATE TABLE transactions (
                              transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              sender_account_id VARCHAR(50) NOT NULL,
                              receiver_account_id VARCHAR(50) NOT NULL,
                              transactional_note VARCHAR(255),
                              amount DECIMAL(15,2) NOT NULL,
                              created_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id),
                              FOREIGN KEY (receiver_account_id) REFERENCES accounts(account_id)
);
