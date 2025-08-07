package com.forex.exception;

/**
 * Exception thrown when an unsupported currency is used
 */
public class UnsupportedCurrencyException extends RuntimeException {
    
    public UnsupportedCurrencyException(String message) {
        super(message);
    }
    
}
