package com.banking_portal.constants;

public class DbConstants {

    //User Table
    public static final String USER_COL_USER_ID = "uid";
    public static final String USER_COL_FIRST_NAME = "first_name";
    public static final String USER_COL_LAST_NAME = "last_name";
    public static final String USER_COL_EMAIL = "email";
    public static final String USER_COL_MOBILE_NUMBER = "phone_number";
    public static final String USER_COL_AADHAR_NUMBER = "aadhar";
    public static final String USER_COL_ADDRESS = "address";
    public static final String USER_COL_DOB = "dob";
    public static final String USER_COL_PASSWORD = "password";

    //Accounts Table
    public static final String ACCOUNT_COL_ACCOUNT_ID = "account_id";
    public static final String ACCOUNT_COL_USER_ID = "user_id";
    public static final String ACCOUNT_COL_BALANCE = "balance";
    public static final String ACCOUNT_COL_TYPE = "type";

    //Transactions Table
    public static final String TRANSACTION_COL_TRANSACTION_ID = "transaction_id";
    public static final String TRANSACTION_COL_SENDER_ACCOUNT_ID = "sender_account_id";
    public static final String TRANSACTION_COL_RECEIVER_ACCOUNT_ID = "receiver_account_id";
    public static final String TRANSACTION_COL_TRANSACTIONAL_NOTE = "transactional_note";
    public static final String TRANSACTION_COL_AMOUNT = "amount";



}

