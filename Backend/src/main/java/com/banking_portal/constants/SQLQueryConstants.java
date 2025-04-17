package com.banking_portal.constants;

public class SQLQueryConstants {

    public static final String CREATE_USER = "INSERT INTO user (first_name, last_name, email, phone_number, aadhar, address, dob, password, re_password) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String CREATE_ACCOUNT = "INSERT INTO accounts (account_id, user_id, balance, type) VALUES (?, ?, ?, ?)";

    public static final String INSERT_TRANSACTION = "INSERT INTO transactions (sender_account_id, receiver_account_id, transactional_note, amount) VALUES (?, ?, ?, ?)";

    public static final String FIND_BY_PHONE_NUMBER = "SELECT uid, phone_number, first_name, password FROM user WHERE phone_number = ?";

    public static final String GET_ACCOUNTS_BY_USER_ID = "SELECT account_id, user_id, balance, type FROM accounts WHERE user_id = ?";

    public static final String FIND_LATEST_ACCOUNT_ID="SELECT account_id FROM accounts ORDER BY CAST(SUBSTRING(account_id, 3) AS UNSIGNED) DESC LIMIT 1";

    public static final String CHECK_ACCOUNT_EXISTS = "SELECT COUNT(account_id) FROM accounts WHERE account_id = ?";

    public static final String GET_ACCOUNT_BALANCE = "SELECT balance FROM accounts WHERE account_id = ?";

    public static final String FIND_USER_BY_USER_ID = "SELECT uid, first_name, last_name, email, phone_number, aadhar, address, dob, password FROM user WHERE uid = ?";

    public static final String GET_TRANSACTION_HISTORY = "SELECT transaction_id, sender_account_id, receiver_account_id, transactional_note, amount, created_ts, updated_ts FROM transactions WHERE sender_account_id = ? OR receiver_account_id = ? ORDER BY created_ts DESC LIMIT ? OFFSET ?;";

    public static final String GET_TRANSACTION_COUNT = "SELECT COUNT(transaction_id) FROM transactions WHERE sender_account_id = ? OR receiver_account_id = ?";

    public static final String UPDATE_SENDER_BALANCE = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

    public static final String UPDATE_RECEIVER_BALANCE = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";

}


