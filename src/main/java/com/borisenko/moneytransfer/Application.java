package com.borisenko.moneytransfer;

import com.borisenko.moneytransfer.controller.AccountsController;
import com.borisenko.moneytransfer.controller.CurrencyExchangeController;
import com.google.gson.Gson;
import spark.ResponseTransformer;
import spark.Spark;

import static com.borisenko.moneytransfer.controller.TestingUtils.populateWithTestData;
import static spark.Spark.*;

public class Application {
    private static final String JSON = "JSON";
    private static final ResponseTransformer TO_JSON = new Gson()::toJson;

    public static void main(String[] Args) {
        start();
        awaitInitialization();
        populateWithTestData();
        //stop();
    }

    private static void start() {
        port(8080);
        before((request, response) -> response.type("application/json"));
        notFound((req, res) -> {
            res.type("text/html");
            return "<html><body><h2>404 Not found</h2></body></html>";
        });

        post("/api/accounts/", JSON, AccountsController::createAccount, TO_JSON);
        get("/api/accounts/:id/", JSON, AccountsController::getAccount, TO_JSON);
        get("/api/accounts/", JSON, AccountsController::getAllAccounts, TO_JSON);
        delete("/api/accounts/:id/", JSON, AccountsController::deleteAccount, TO_JSON);
        post("/api/movements/withdraw/", JSON, AccountsController::withDraw, TO_JSON);
        post("/api/movements/deposit/", JSON, AccountsController::deposit, TO_JSON);
        post("/api/movements/transfer/", JSON, AccountsController::transfer, TO_JSON);

        post("/api/exchangerates/", JSON, CurrencyExchangeController::createCurrencyExchangePair, TO_JSON);
        get("/api/exchangerates/", JSON, CurrencyExchangeController::getAllCurrencyExchangePairs, TO_JSON);
        post("/api/exchangerates/update/", JSON, CurrencyExchangeController::updateCurrencyExchangePair, TO_JSON);
    }

    public static void stop() {
        Spark.stop();
    }
}