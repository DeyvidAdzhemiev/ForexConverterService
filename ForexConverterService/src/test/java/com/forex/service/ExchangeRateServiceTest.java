package com.forex.service;

import com.forex.response.ExchangeRateResponse;
import com.forex.service.impl.MockExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    private ExchangeRateService exchangeRateService;
    
    @BeforeEach
    void setUp() {
        exchangeRateService = new MockExchangeRateService();
    }
    
    @Test
    void getExchangeRate_Success() {
        // Given
        String sourceCurrency = "EUR";  
        String targetCurrency = "USD";
        
        // When
        Optional<ExchangeRateResponse> result = exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency);
        
        // Then
        assertTrue(result.isPresent());
        ExchangeRateResponse response = result.get();
        assertEquals(sourceCurrency, response.getSourceCurrency());
        assertEquals(targetCurrency, response.getTargetCurrency());
        assertTrue(response.getExchangeRate().compareTo(BigDecimal.ZERO) > 0);
    }
}
