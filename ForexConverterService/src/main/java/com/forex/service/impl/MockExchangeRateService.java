package com.forex.service.impl;

import com.forex.response.ExchangeRateResponse;
import com.forex.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Mock implementation of ExchangeRateService that returns predefined rates
 * for common currencies or generates random rates for others.
 */
@Service
@Slf4j
public class MockExchangeRateService implements ExchangeRateService {

    private final Map<String, Double> eurRates;
    private final Random random;

    public MockExchangeRateService() {
        this.eurRates = new HashMap<>();
        // Predefined rates relative to EUR
        eurRates.put("USD", 1.09);
        eurRates.put("GBP", 0.85);
        eurRates.put("JPY", 157.50);
        eurRates.put("CHF", 0.96);
        eurRates.put("CAD", 1.47);
        eurRates.put("AUD", 1.63);
        
        this.random = new Random();
    }

    @Override
    public Optional<ExchangeRateResponse> getExchangeRate(String sourceCurrency, String targetCurrency) {
        log.info("Getting mock exchange rate for {}/{}", sourceCurrency, targetCurrency);

        try {
            Double rate = eurRates.getOrDefault(targetCurrency, 0.5 + random.nextDouble() * 2.0);

            ExchangeRateResponse response = new ExchangeRateResponse(sourceCurrency, targetCurrency, BigDecimal.valueOf(rate));
            
            log.info("Returning mock rate for {}/{}: {}", sourceCurrency, targetCurrency, rate);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Error generating mock exchange rate: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
