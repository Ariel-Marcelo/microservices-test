package com.demo.trclientes.infrastructure.adapters.in.exceptions;

public class
LowBalanceException extends RuntimeException{
    public LowBalanceException(String message) {
        super(message);
    }
}
