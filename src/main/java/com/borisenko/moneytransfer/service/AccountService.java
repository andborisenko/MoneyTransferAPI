package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.exceptions.InsufficientMoneyException;
import com.borisenko.moneytransfer.model.BankAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    long createAccount(long userId, String currencyCode);

    Optional<BankAccount> getAccountById(long accountId);

    List<BankAccount> getAccountsByUserId(long userId);

    List<BankAccount> getAllAccounts();

    void deleteAccount(long accountId);

    boolean withDraw(long accountId, String currencyCode, BigDecimal amount) throws InsufficientMoneyException;

    boolean deposit(long accountId, String currencyCode, BigDecimal amount);

    boolean transfer(long originAccountId, long targetAccountId, String currencyCode, BigDecimal amount) throws InsufficientMoneyException;

}

