package com.borisenko.moneytransfer.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestingUtils {
    private TestingUtils(){}
    public static void populateWithTestData() {
        callCreateAccountMethod("{\"userId\":1001,\"currencyCode\":\"USD\"}");
        callCreateAccountMethod("{\"userId\":1002,\"currencyCode\":\"AUD\"}");
        callCreateAccountMethod("{\"userId\":1003,\"currencyCode\":\"RUB\"}");

        callDepositMethod("{\"accountId\":1001,\"currencyCode\":\"USD\",\"balance\":500}");
        callDepositMethod("{\"accountId\":1002,\"currencyCode\":\"AUD\",\"balance\":200}");

        callCreateExchangePairMethod("{\"currencyFrom\":\"USD\",\"currencyTo\":\"AUD\",\"minAmount\":10,\"rate\":1.38}");
        callCreateExchangePairMethod("{\"currencyFrom\":\"USD\",\"currencyTo\":\"RUB\",\"minAmount\":10,\"rate\":67.5}");

        callTransferMethod("{\"originAccountId\":1001,\"targetAccountId\":1002,\"amount\":100,\"currencyCode\":\"USD\"}");
        callTransferMethod("{\"originAccountId\":1001,\"targetAccountId\":1003,\"amount\":200,\"currencyCode\":\"USD\"}");

    }

    private static void callCreateExchangePairMethod(String exchangePairBody) {
        callMethodByURL("/api/exchangerates/", exchangePairBody);
    }

    private static void callTransferMethod(String transferBody) {
        callMethodByURL("/api/movements/transfer/", transferBody);
    }

    private static void callDepositMethod(String depositBody) {
        callMethodByURL("/api/movements/deposit/", depositBody);
    }

    private static void callCreateAccountMethod(String createAccountBody) {
        callMethodByURL("/api/accounts/", createAccountBody);
    }

    private static void callMethodByURL(String methodURL, String methodBody) {
        try {
            URL url = new URL("http://localhost:8080" + methodURL);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = methodBody.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
