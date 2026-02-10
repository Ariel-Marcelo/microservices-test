package com.demo.trcuentas.domain.shared.exceptions;

public class LowBalanceException extends RuntimeException{
    public LowBalanceException(String message) {
        super(message);
    }
}
