package com.borisenko.moneytransfer.storage;

import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.model.CurrencyExchangePair;
import com.borisenko.moneytransfer.model.User;

public interface RepositoryStorage {
    Repository<Long, CurrencyExchangePair> getCurExchangeRepository();

    Repository<Long, BankAccount> getBankAccountsRepository();

    Repository<Long, User> getUsersRepository();
}
