package com.borisenko.moneytransfer.controller;

import com.borisenko.moneytransfer.exceptions.InsufficientMoneyException;
import com.borisenko.moneytransfer.model.BankAccount;
import com.borisenko.moneytransfer.model.MoneyTransfer;
import com.borisenko.moneytransfer.service.AccountService;
import com.borisenko.moneytransfer.service.AccountServiceImpl;
import com.borisenko.moneytransfer.service.CurrencyExchangeService;
import com.borisenko.moneytransfer.service.CurrencyExchangeServiceImpl;
import com.borisenko.moneytransfer.storage.InMemoryStorage;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountsController {
    private static final CurrencyExchangeService curExchService = new CurrencyExchangeServiceImpl(InMemoryStorage.INSTANCE.getCurExchangeRepository());
    private static final AccountService accountService = new AccountServiceImpl(InMemoryStorage.INSTANCE.getBankAccountsRepository(), curExchService);
    private static final Logger LOG = LoggerFactory.getLogger(AccountsController.class);
    private static final String DONE = "Done";
    private static final String FAILED = "Failed";

    public static long createAccount(final Request request, final Response response) {
        BankAccount account = new Gson().fromJson(request.body(), BankAccount.class);
        if (account == null || account.getUserId() == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return -1;
        }
        response.status(HttpStatus.CREATED_201);
        return accountService.createAccount(account.getUserId(), account.getCurrencyCode());
    }

    public static BankAccount getAccount(Request request, Response response) {
        Optional<BankAccount> accountById = accountService.getAccountById(Long.valueOf(request.params("id")));
        if (accountById.isPresent()) {
            response.status(HttpStatus.OK_200);
            return accountById.get();
        } else {
            response.status(HttpStatus.NOT_FOUND_404);
            return null;
        }
    }

    public static List<BankAccount> getAllAccounts(Request request, Response response) {
        List<BankAccount> allAccounts = accountService.getAllAccounts();
        response.status(HttpStatus.OK_200);
        return allAccounts;
    }


    public static String deleteAccount(Request request, Response response) {
        accountService.deleteAccount(Long.valueOf(request.params("id")));
        response.status(HttpStatus.OK_200);
        return DONE;
    }

    public static String deposit(Request request, Response response) {
        BankAccount account = new Gson().fromJson(request.body(), BankAccount.class);
        if (account == null || account.getAccountId() == null || account.getBalance() == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return FAILED;
        } else {
            boolean result = accountService.deposit(account.getAccountId(), account.getCurrencyCode(), account.getBalance());
            response.status(result ? HttpStatus.OK_200 : HttpStatus.BAD_REQUEST_400);
            return result ? DONE : FAILED;
        }
    }

    public static String transfer(Request request, Response response) {
        MoneyTransfer transfer = new Gson().fromJson(request.body(), MoneyTransfer.class);
        Long originAccountId = transfer.getOriginAccountId();
        Long targetAccountId = transfer.getTargetAccountId();
        BigDecimal amount = transfer.getAmount();
        if (originAccountId == null || targetAccountId == null || amount == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return FAILED;
        }
        boolean result = false;
        try {
            result = accountService.transfer(originAccountId, targetAccountId, transfer.getCurrencyCode(), amount);
        } catch (InsufficientMoneyException e) {
            LOG.error(e.getMessage(), e);
            response.status(HttpStatus.PAYMENT_REQUIRED_402);
            return e.getMessage();
        }
        response.status(HttpStatus.OK_200);
        return result ? DONE : FAILED;
    }

    public static String withDraw(Request request, Response response) {
        BankAccount account = new Gson().fromJson(request.body(), BankAccount.class);
        if (account == null || account.getAccountId() == null || account.getBalance() == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return FAILED;
        } else {
            boolean result = false;
            try {
                result = accountService.withDraw(account.getAccountId(), account.getCurrencyCode(), account.getBalance());
            } catch (InsufficientMoneyException e) {
                LOG.error(e.getMessage(), e);
                response.status(HttpStatus.PAYMENT_REQUIRED_402);
                return FAILED;
            }
            response.status(result ? HttpStatus.OK_200 : HttpStatus.BAD_REQUEST_400);
            return result ? DONE : FAILED;
        }
    }
}
