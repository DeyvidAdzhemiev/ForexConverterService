package com.forex.exception;

public class ExchangeRateException extends RuntimeException {
    
    public ExchangeRateException(String message) {
        super(message);
    }

}
