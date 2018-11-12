package com.borisenko.moneytransfer.service;

import com.borisenko.moneytransfer.exceptions.UnknownCurrencyExchangePair;
import com.borisenko.moneytransfer.model.CurrencyExchangePair;
import com.borisenko.moneytransfer.storage.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final AtomicLong curExchPairIdGenerator = new AtomicLong(100);
    private Repository<Long, CurrencyExchangePair> repository;

    public CurrencyExchangeServiceImpl(Repository<Long, CurrencyExchangePair> repository) {
        this.repository = repository;
    }

    @Override
    public BigDecimal exchange(String currencyCodeFrom, String currencyCodeTo, BigDecimal amount) throws UnknownCurrencyExchangePair {
        Optional<CurrencyExchangePair> exchangePair = StreamSupport.stream(repository.getAllEntities().spliterator(), false)
                .filter(curExch -> isApplicable(currencyCodeFrom, currencyCodeTo, amount, curExch))
                .min(Comparator.comparing(CurrencyExchangePair::getMinAmount));

        if (exchangePair.isPresent()) {
            BigDecimal rate = exchangePair.get().getRate();
            return rate.multiply(amount);
        } else
            throw new UnknownCurrencyExchangePair(currencyCodeFrom, currencyCodeTo);

    }

    @Override
    public CurrencyExchangePair updateCurrencyExchangePair(CurrencyExchangePair newCurrencyExchangePair) {
        return repository.update(newCurrencyExchangePair);
    }

    @Override
    public long createCurrencyExchangePair(String currencyCodeFrom, String currencyCodeTo, BigDecimal minAmount, BigDecimal rate) {
        long id = curExchPairIdGenerator.incrementAndGet();
        repository.create(new CurrencyExchangePair(id, currencyCodeFrom, currencyCodeTo, minAmount, rate));
        return id;
    }

    @Override
    public List<CurrencyExchangePair> getAllExchangePairs() {
        List<CurrencyExchangePair> result = new LinkedList<>();
        repository.getAllEntities().forEach(result::add);
        return result;
    }

    @Override
    public void deleteCurrencyExchangePair(String currencyFrom, String currencyTo, BigDecimal minAmount, BigDecimal rate) {
        StreamSupport.stream(repository.getAllEntities().spliterator(), false)
                .filter(checkSameCurrentExchangeRate(currencyFrom, currencyTo, minAmount, rate))
                .findFirst()
                .ifPresent(pair -> repository.delete(pair.getId()));
    }

    private Predicate<CurrencyExchangePair> checkSameCurrentExchangeRate(String currencyFrom, String currencyTo, BigDecimal minAmount, BigDecimal rate) {
        return curExchPair -> curExchPair.getRate().equals(rate) &&
                curExchPair.getCurrencyFrom().equals(currencyFrom) &&
                curExchPair.getCurrencyTo().equals(currencyTo) &&
                curExchPair.getMinAmount().equals(minAmount);
    }

    private boolean isApplicable(String currencyFrom, String currencyTo, BigDecimal amount, CurrencyExchangePair curExch) {
        return curExch.getCurrencyFrom().equals(currencyFrom) && curExch.getCurrencyTo().equals(currencyTo) &&
                curExch.getMinAmount().compareTo(amount) <= 0;
    }
}
