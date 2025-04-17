package com.banking_portal.exception;

public class CustomSQLException extends RuntimeException
{
    public CustomSQLException(String message, Throwable cause) {
        super(message, cause);
    }
}