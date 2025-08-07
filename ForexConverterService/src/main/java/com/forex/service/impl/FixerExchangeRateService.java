package com.forex.service.impl;

import com.forex.exception.UnsupportedCurrencyException;
import com.forex.response.ExchangeRateResponse;
import com.forex.response.FixerResponse;
import com.forex.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class FixerExchangeRateService implements ExchangeRateService {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    public FixerExchangeRateService(
            @Qualifier("fixerApiWebClient") WebClient webClient,
            @Value("${forex.provider.fixer.api-key}") String apiKey,
            @Value("${forex.provider.fixer.base-url}") String baseUrl) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;

        log.info("Initialized FixerExchangeRateService with baseUrl: {}", baseUrl);
    }

    @Override
    public Optional<ExchangeRateResponse> getExchangeRate(String sourceCurrency, String targetCurrency) {
        log.info("Getting exchange rate for {}/{} using Fixer.io latest endpoint", sourceCurrency, targetCurrency);
        
        if (!"EUR".equals(sourceCurrency)) {
            log.error("Only EUR is supported as source currency with the free tier of Fixer.io");
            throw new UnsupportedCurrencyException(
                "Only EUR is supported as base currency with the free tier of Fixer.io. Please use EUR as the source currency."
            );
        }

        String requestUrl = "/latest" +
                "?access_key=" + apiKey +
                "&base=EUR" +
                "&symbols=" + targetCurrency;
        log.info("Making request to Fixer.io API: {}{}", baseUrl, requestUrl);

        try {
            FixerResponse response = webClient.get()
                    .uri(requestUrl)
                    .retrieve()
                    .bodyToMono(FixerResponse.class)
                    .block();
            
            if (response == null) {
                log.error("Null response from Fixer.io API");
                return Optional.empty();
            }
            
            log.info("Fixer API response: success={}, base={}, rates={}", 
                    response.isSuccess(), response.getBase(), response.getRates());
            
            if (!response.isSuccess()) {
                String errorInfo = response.getError() != null ?
                    response.getError().getCode() + ": " + response.getError().getInfo() :
                    "Unknown error";
                log.error("Fixer API error: {}", errorInfo);
                return Optional.empty();
            }
            
            if (response.getRates() == null || !response.getRates().containsKey(targetCurrency)) {
                log.error("Target currency {} not found in response rates: {}", targetCurrency, response.getRates());
                return Optional.empty();
            }
            
            BigDecimal rate = BigDecimal.valueOf(response.getRates().get(targetCurrency));
            log.info("Successfully retrieved exchange rate: {} {} = {} {}", 
                    1, sourceCurrency, rate, targetCurrency);
            return Optional.of(new ExchangeRateResponse(sourceCurrency, targetCurrency, rate));
            
        } catch (Exception e) {
            log.error("Error getting exchange rate: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return Optional.empty();
        }
    }
}
