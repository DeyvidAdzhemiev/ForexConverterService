package com.forex.config;

import com.forex.service.ExchangeRateService;
import com.forex.service.impl.FixerExchangeRateService;
import com.forex.service.impl.MockExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class ExchangeRateServiceConfig {

    @Value("${forex.provider.use-mock}")
    private boolean useMockProvider;

    @Value("${forex.provider.fixer.api-key}")
    private String fixerApiKey;

    @Value("${forex.provider.fixer.base-url}")
    private String fixerBaseUrl;

    @Bean
    @Primary
    public ExchangeRateService exchangeRateService(@Qualifier("fixerApiWebClient") WebClient fixerApiWebClient) {
        if (useMockProvider) {
            log.info("Using mock exchange rate provider");
            return new MockExchangeRateService();
        } else {
            log.info("Using Fixer.io exchange rate provider with base URL: {}", fixerBaseUrl);
            return new FixerExchangeRateService(fixerApiWebClient, fixerApiKey, fixerBaseUrl);
        }
    }
}
