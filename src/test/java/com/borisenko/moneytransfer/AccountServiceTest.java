package com.borisenko.moneytransfer;

import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.service.AccountService;
import com.borisenko.moneytransfer.service.AccountServiceImpl;
import com.borisenko.moneytransfer.service.CurrencyExchangeService;
import com.borisenko.moneytransfer.service.CurrencyExchangeServiceImpl;
import com.borisenko.moneytransfer.storage.InMemoryStorage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {

    private final CurrencyExchangeService currencyExchangeService = new CurrencyExchangeServiceImpl(InMemoryStorage.INSTANCE.getCurExchangeRepository());
    private final AccountService accountService = new AccountServiceImpl(InMemoryStorage.INSTANCE.getBankAccountsRepository(), currencyExchangeService);

    @Test
    public void testAccountCreation() {
        String aud = "AUD"; //Australian Dollar
        long userId = 1;
        Long accountId = accountService.createAccount(userId, aud);

        List<BankAccount> accounts = accountService.getAccountsByUserId(userId);
        assertEquals(1, accounts.size());

        assertEquals(accountId, accounts.get(0).getAccountId());
    }

    @Test
    public void testThreeAccountsForOneUserCreation() {
        String aud = "AUD"; //Australian Dollar
        String usd = "USD"; //United States Dollar
        String rub = "RUB"; //Russian Rouble

        long userId = 2;
        long firstAccountId = accountService.createAccount(userId, aud);
        long secondAccountId = accountService.createAccount(userId, usd);
        long thirdAccountID = accountService.createAccount(userId, rub);
        Set<Long> createdAccountsIDs = new HashSet<>(Arrays.asList(firstAccountId, secondAccountId, thirdAccountID));

        List<BankAccount> accounts = accountService.getAccountsByUserId(userId);
        assertEquals(3, accounts.size());

        accounts.forEach(bankAccount -> assertTrue(createdAccountsIDs.contains(bankAccount.getAccountId())));

    }

    @Test
    public void testFiveAccountsWithSameCurrencyCreationForOneUser() {
        String rub = "RUB"; //Russian Rouble
        long userId = 3;
        IntStream.range(0, 5).forEach(i -> accountService.createAccount(userId, rub));

        List<BankAccount> accounts = accountService.getAccountsByUserId(userId);
        assertEquals(5, accounts.size());
        accounts.forEach(account -> assertEquals(rub, account.getCurrencyCode()));
    }

}
