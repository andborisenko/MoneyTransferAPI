package com.borisenko.moneytransfer;

import com.borisenko.moneytransfer.exceptions.InsufficientMoneyException;
import com.borisenko.moneytransfer.exceptions.UnknownCurrencyExchangePair;
import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.service.*;
import com.borisenko.moneytransfer.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTransferServiceTest {

    private static final UserService userService = new UserServiceImpl(InMemoryStorage.INSTANCE.getUsersRepository());
    private static final CurrencyExchangeService curExchService = new CurrencyExchangeServiceImpl(InMemoryStorage.INSTANCE.getCurExchangeRepository());
    private static final AccountService accountService = new AccountServiceImpl(InMemoryStorage.INSTANCE.getBankAccountsRepository(), curExchService);


    private static final String RUB = "RUB";
    private static final String USD = "USD";
    private static final String AUD = "AUD";

    @BeforeAll
    static void fillCurrencyExchangeRates() {
        curExchService.createCurrencyExchangePair(USD, AUD, BigDecimal.ZERO, BigDecimal.valueOf(1.38));
    }


    @Test
    public void testMoneyTransferBetweenOneUserSameCurrencyAccounts() throws UnknownCurrencyExchangePair {
        long userId = userService.createUser("John", "Doe", "john.doe@nasdaq.com");

        testMoneyTransfer(userId, userId, RUB, RUB);
    }

    @Test
    public void testMoneyTransferBetweenOneUserDifferentCurrenciesAccounts() throws UnknownCurrencyExchangePair {
        long userId = userService.createUser("John", "Doe", "john.doe@nasdaq.com");

        testMoneyTransfer(userId, userId, USD, AUD);
    }

    @Test
    public void testMoneyTransferBetweenDifferentUsersWithDifferentCurrenciesAccounts() throws UnknownCurrencyExchangePair {
        long firstUserId = userService.createUser("John", "Doe", "john.doe@nasdaq.com");
        long secondUserId = userService.createUser("Mike", "Lee", "mleeee@hotmail.com");

        testMoneyTransfer(firstUserId, secondUserId, USD, AUD);
    }

    private void testMoneyTransfer(long firstUserId, long secondUserId, String curCode1, String curCode2) throws UnknownCurrencyExchangePair {

        BigDecimal amount = BigDecimal.valueOf(23.9);
        BigDecimal exchangedAmount = (curCode1.equals(curCode2) ? amount : curExchService.exchange(curCode1, curCode2, amount));

        long firstAccountId = accountService.createAccount(firstUserId, curCode1);
        long secondAccountId = accountService.createAccount(secondUserId, curCode2);

        assertThrows(InsufficientMoneyException.class, () -> {
            accountService.transfer(firstAccountId, secondAccountId, curCode1, amount);
        });

        try {
            accountService.deposit(firstAccountId, curCode1, amount);
            accountService.transfer(firstAccountId, secondAccountId, curCode1, amount);

            Optional<BankAccount> account = accountService.getAccountById(secondAccountId);
            assertTrue(account.isPresent());

            BigDecimal actualBalance = account.get().getBalance();
            assertEquals(exchangedAmount, actualBalance);
        } catch (InsufficientMoneyException | UnknownCurrencyExchangePair e) {
            e.printStackTrace();
        }
    }
}
