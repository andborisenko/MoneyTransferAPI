package com.borisenko.moneytransfer.controller;

import com.borisenko.moneytransfer.model.CurrencyExchangePair;
import com.borisenko.moneytransfer.service.CurrencyExchangeService;
import com.borisenko.moneytransfer.service.CurrencyExchangeServiceImpl;
import com.borisenko.moneytransfer.storage.InMemoryStorage;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.List;

public class CurrencyExchangeController {
    private static final CurrencyExchangeService curExchService = new CurrencyExchangeServiceImpl(InMemoryStorage.INSTANCE.getCurExchangeRepository());

    public static long createCurrencyExchangePair(final Request request, final Response response) {
        CurrencyExchangePair exchangePair = new Gson().fromJson(request.body(), CurrencyExchangePair.class);
        if (exchangePair.getCurrencyFrom() == null || exchangePair.getCurrencyTo() == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return -1;
        }
        response.status(HttpStatus.CREATED_201);
        return curExchService.createCurrencyExchangePair(
                exchangePair.getCurrencyFrom(),
                exchangePair.getCurrencyTo(),
                exchangePair.getMinAmount(),
                exchangePair.getRate());
    }

    public static CurrencyExchangePair updateCurrencyExchangePair(final Request request, final Response response) {
        CurrencyExchangePair pair = new Gson().fromJson(request.body(), CurrencyExchangePair.class);
        if (pair.getCurrencyFrom() == null || pair.getCurrencyTo() == null || pair.getId() == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return pair;
        }
        response.status(HttpStatus.CREATED_201);
        return curExchService.updateCurrencyExchangePair(pair);
    }

    public static List<CurrencyExchangePair> getAllCurrencyExchangePairs(Request request, Response response) {

        List<CurrencyExchangePair> allPairs = curExchService.getAllExchangePairs();
        response.status(HttpStatus.OK_200);
        return allPairs;
    }
}