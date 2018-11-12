package com.borisenko.moneytransfer.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CurrencyExchangePair implements PersistentEntity<Long> {
    private Long id;
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal minAmount;
    private BigDecimal rate;

    public CurrencyExchangePair() {
    }

    public CurrencyExchangePair(Long id, String currencyCodeFrom, String currencyCodeTo, BigDecimal minAmount, BigDecimal rate) {
        this.id = id;
        this.currencyFrom = currencyCodeFrom;
        this.currencyTo = currencyCodeTo;
        this.minAmount = minAmount;
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyExchangePair that = (CurrencyExchangePair) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(currencyFrom, that.currencyFrom) &&
                Objects.equals(currencyTo, that.currencyTo) &&
                Objects.equals(minAmount, that.minAmount) &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyFrom, currencyTo, minAmount, rate);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    @Override
    public Long getId() {
        return id;
    }

}
