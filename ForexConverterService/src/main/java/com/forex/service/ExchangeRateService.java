package com.forex.service;

import com.forex.response.ExchangeRateResponse;

import java.util.Optional;

public interface ExchangeRateService {

    Optional<ExchangeRateResponse> getExchangeRate(String sourceCurrency, String targetCurrency);
}
