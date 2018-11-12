package com.borisenko.moneytransfer.exceptions;

public class UnknownCurrencyExchangePair extends RuntimeException {
    public UnknownCurrencyExchangePair(String currencyCode1, String currencyCode2) {
        super("There is no data for currency exchange pair: " + currencyCode1 + '-' + currencyCode2 + '.');
    }
}
