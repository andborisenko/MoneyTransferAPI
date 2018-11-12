package com.borisenko.moneytransfer.storage;

import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.model.CurrencyExchangePair;
import com.borisenko.moneytransfer.model.User;

public class InMemoryStorage implements RepositoryStorage {
    public static final InMemoryStorage INSTANCE = new InMemoryStorage();

    private static final Repository<Long, BankAccount> bankAccountsRepository = new AbstractRepository<>();
    private static final Repository<Long, User> usersRepository = new AbstractRepository<>();
    private static final Repository<Long, CurrencyExchangePair> curExchangeRepository = new AbstractRepository<>();

    private InMemoryStorage() {
    }

    @Override
    public Repository<Long, CurrencyExchangePair> getCurExchangeRepository() {
        return curExchangeRepository;
    }

    @Override
    public Repository<Long, BankAccount> getBankAccountsRepository() {
        return bankAccountsRepository;
    }

    @Override
    public Repository<Long, User> getUsersRepository() { return usersRepository; }
}
