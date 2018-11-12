package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.exceptions.UnknownCurrencyExchangePair;
import com.borisenko.moneytransfer.model.CurrencyExchangePair;

import java.math.BigDecimal;
import java.util.List;


public interface CurrencyExchangeService {
    long createCurrencyExchangePair(String currencyCodeFrom, String currencyCodeTo, BigDecimal minAmount, BigDecimal rate);

    List<CurrencyExchangePair> getAllExchangePairs();

    BigDecimal exchange(String currencyCodeFrom, String currencyCodeTo, BigDecimal amount) throws UnknownCurrencyExchangePair;

    CurrencyExchangePair updateCurrencyExchangePair(CurrencyExchangePair newCurrencyExchangePair);

    void deleteCurrencyExchangePair(String currencyCodeFrom, String currencyCodeTo, BigDecimal minAmount, BigDecimal rate);
}
