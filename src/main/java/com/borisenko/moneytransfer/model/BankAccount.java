package com.borisenko.moneytransfer.model;

import java.math.BigDecimal;
import java.util.Objects;

public class BankAccount implements PersistentEntity<Long> {
    private Long accountId;
    private Long userId;
    private String currencyCode;
    private BigDecimal balance;

    public BankAccount() {
    }

    public BankAccount(Long accountId, Long userId, String currencyCode) {
        this.accountId = accountId;
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.balance = new BigDecimal(0);
    }

    public BankAccount(Long accountId, Long userId, String currencyCode, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount account = (BankAccount) o;
        return Objects.equals(accountId, account.accountId) &&
                Objects.equals(userId, account.userId) &&
                Objects.equals(currencyCode, account.currencyCode) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, currencyCode, balance);
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Long getAccountId() {
        return accountId;
    }

    @Override
    public Long getId() {
        return accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
