package com.borisenko.moneytransfer.exceptions;

public class InsufficientMoneyException extends Exception {
    public InsufficientMoneyException(long accountId) {
        super("Not enough money for withdrawal from account ID: " + accountId);
    }
}