package com.banking_portal.exception;

public class InsufficientFundsException extends RuntimeException
{
    public InsufficientFundsException(String message)
    {
        super(message);
    }
}
