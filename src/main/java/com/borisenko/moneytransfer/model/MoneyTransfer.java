package com.borisenko.moneytransfer.model;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyTransfer {
    private Long originAccountId;
    private Long targetAccountId;
    private String currencyCode;
    private BigDecimal amount;

    public MoneyTransfer(Long originAccountId, Long targetAccountId, String currencyCode, BigDecimal amount) {
        this.originAccountId = originAccountId;
        this.targetAccountId = targetAccountId;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public MoneyTransfer() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyTransfer that = (MoneyTransfer) o;
        return Objects.equals(originAccountId, that.originAccountId) &&
                Objects.equals(targetAccountId, that.targetAccountId) &&
                Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originAccountId, targetAccountId, currencyCode, amount);
    }

    public Long getOriginAccountId() {
        return originAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
