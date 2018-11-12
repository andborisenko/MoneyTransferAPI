package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.exceptions.InsufficientMoneyException;
import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.storage.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountServiceImpl implements AccountService {
    private static final AtomicLong accountIdGenerator = new AtomicLong(1000);
    private static final Map<Long, ReadWriteLock> locksMap = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);
    private static final ReadWriteLock allAccountsLock = new ReentrantReadWriteLock();


    private Repository<Long, BankAccount> repository;
    private CurrencyExchangeService currencyExchangeService;

    public AccountServiceImpl(Repository<Long, BankAccount> repository, CurrencyExchangeService currencyExchangeService) {
        this.repository = repository;
        this.currencyExchangeService = currencyExchangeService;
    }

    @Override
    public long createAccount(long userId, String currencyCode) {
        long accountId = accountIdGenerator.incrementAndGet();
        BankAccount account = new BankAccount(accountId, userId, currencyCode);
        repository.create(account);
        return accountId;
    }

    @Override
    public Optional<BankAccount> getAccountById(long id) {
        Lock readLock = getLock(id).readLock();

        readLock.lock();
        Optional<BankAccount> account;
        try {
            account = repository.getById(id);
        } finally {
            readLock.unlock();
        }
        return account;
    }

    @Override
    public List<BankAccount> getAccountsByUserId(long id) {
        List<BankAccount> result = new LinkedList<>();

        allAccountsLock.readLock().lock();
        try {
            for (BankAccount account : repository.getAllEntities()) {
                if (account.getUserId() == id) result.add(account);
            }
        } finally {
            allAccountsLock.readLock().unlock();
        }
        return result;
    }

    @Override
    public void deleteAccount(long accountId) {
        repository.delete(accountId);
    }

    @Override
    public List<BankAccount> getAllAccounts() {
        List<BankAccount> accounts = new LinkedList<>();

        allAccountsLock.readLock().lock();
        try {
            repository.getAllEntities().forEach(accounts::add);
        } finally {
            allAccountsLock.readLock().unlock();
        }
        return accounts;
    }

    @Override
    public boolean withDraw(long accountId, String currencyCode, BigDecimal amount) throws InsufficientMoneyException {
        Optional<BankAccount> accountById = getAccountById(accountId);
        if (accountById.isPresent()) {
            BankAccount account = accountById.get();

            if (account.getBalance().compareTo(amount) < 0) throw new InsufficientMoneyException(accountId);

            updateBalanceWithLock(account, account.getBalance().subtract(amount));
            return true;
        }
        return false;
    }

    @Override
    public boolean deposit(long accountId, String currencyCode, BigDecimal amount) {
        Optional<BankAccount> accountById = getAccountById(accountId);
        if (accountById.isPresent()) {
            BankAccount account = accountById.get();

            updateBalanceWithLock(account, account.getBalance().add(amount));
            return true;
        }
        return false;
    }

    private void updateBalanceWithLock(BankAccount account, BigDecimal newBalance) {
        allAccountsLock.writeLock().lock();

        try {
            Lock lock = getLock(account.getAccountId()).writeLock();

            lock.lock();
            try {
                repository.update(new BankAccount(account.getAccountId(), account.getUserId(), account.getCurrencyCode(), newBalance));
            } finally {
                lock.unlock();
            }
        } finally {
            allAccountsLock.writeLock().unlock();
        }
    }


    @Override
    public boolean transfer(long originAccountId, long targetAccountId, String currencyCode, BigDecimal amount) throws InsufficientMoneyException {
        if (originAccountId == targetAccountId) return true;

        Optional<BankAccount> originAccount = getAccountById(originAccountId);
        Optional<BankAccount> targetAccount = getAccountById(targetAccountId);
        if (!originAccount.isPresent() || !targetAccount.isPresent()) return false;

        BankAccount accountFrom = originAccount.get();
        BankAccount accountTo = targetAccount.get();

        BigDecimal amountFrom = amount;
        if (!accountFrom.getCurrencyCode().equals(currencyCode)) {
            amountFrom = currencyExchangeService.exchange(accountFrom.getCurrencyCode(), currencyCode, amount);
        }
        if (accountFrom.getBalance().compareTo(amountFrom) < 0) throw new InsufficientMoneyException(originAccountId);


        BigDecimal amountTo = amount;
        if (!accountTo.getCurrencyCode().equals(currencyCode)) {
            amountTo = currencyExchangeService.exchange(currencyCode, accountTo.getCurrencyCode(), amount);
        }

        Lock firstLock = getLock(Math.min(originAccountId, targetAccountId)).writeLock();
        Lock secondLock = getLock(Math.max(originAccountId, targetAccountId)).writeLock();

        allAccountsLock.writeLock().lock();
        try {

            firstLock.lock();
            try {
                secondLock.lock();
                try {
                    withDraw(accountFrom.getAccountId(), accountFrom.getCurrencyCode(), amountFrom);
                    deposit(accountTo.getAccountId(), accountTo.getCurrencyCode(), amountTo);
                } finally {
                    secondLock.unlock();
                }

            } finally {
                firstLock.unlock();
            }
        } finally {
            allAccountsLock.writeLock().unlock();
        }
        return true;
    }

    private ReadWriteLock getLock(Long id) {
        locksMap.putIfAbsent(id, new ReentrantReadWriteLock());
        return locksMap.get(id);
    }
}
