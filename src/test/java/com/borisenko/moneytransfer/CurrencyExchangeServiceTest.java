package com.borisenko.moneytransfer;

import com.borisenko.moneytransfer.exceptions.UnknownCurrencyExchangePair;
import com.borisenko.moneytransfer.service.CurrencyExchangeService;
import com.borisenko.moneytransfer.service.CurrencyExchangeServiceImpl;
import com.borisenko.moneytransfer.storage.InMemoryStorage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyExchangeServiceTest {

    private final CurrencyExchangeService curExchService = new CurrencyExchangeServiceImpl(InMemoryStorage.INSTANCE.getCurExchangeRepository());

    @Test
    public void testCurrencyExchangePairCreation() {
        String aud = "AUD"; //Australian Dollar
        String usd = "USD"; //United States Dollar

        double audToUsdRate = 0.72;
        curExchService.createCurrencyExchangePair(aud, usd, BigDecimal.ZERO, BigDecimal.valueOf(audToUsdRate));

        double usdToAudRate = 1.38;
        curExchService.createCurrencyExchangePair(usd, aud, BigDecimal.ZERO, BigDecimal.valueOf(usdToAudRate));

        try {
            BigDecimal amount = BigDecimal.TEN;
            BigDecimal exchangeResult = curExchService.exchange(aud, usd, amount);
            assertEquals(exchangeResult, amount.multiply(BigDecimal.valueOf(audToUsdRate)));

            BigDecimal exchangeResultReverse = curExchService.exchange(usd, aud, amount);
            assertEquals(exchangeResultReverse, amount.multiply(BigDecimal.valueOf(usdToAudRate)));

        } catch (UnknownCurrencyExchangePair ucep) {
            ucep.printStackTrace();
        }
    }
}
