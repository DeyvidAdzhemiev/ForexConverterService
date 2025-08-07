package com.forex.service;

import com.forex.entity.ConversionEntity;
import com.forex.request.ConversionRequest;
import com.forex.response.ConversionResponse;
import com.forex.response.ExchangeRateResponse;
import com.forex.repository.ConversionRepository;
import com.forex.service.impl.ConversionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionRepository conversionRepository;

    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new ConversionServiceImpl(exchangeRateService, conversionRepository);
    }

    @Test
    void convertCurrency_Success() {
        // Given
        String sourceCurrency = "USD";
        String targetCurrency = "EUR";
        BigDecimal sourceAmount = new BigDecimal("100.00");
        BigDecimal exchangeRate = new BigDecimal("0.85");
        BigDecimal expectedTargetAmount = new BigDecimal("85.00");

        ConversionRequest request = new ConversionRequest(sourceAmount, sourceCurrency, targetCurrency);
        ExchangeRateResponse rateResponse = new ExchangeRateResponse(sourceCurrency, targetCurrency, exchangeRate);

        when(exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency)).thenReturn(Optional.of(rateResponse));
        when(conversionRepository.save(any(ConversionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ConversionResponse response = conversionService.convertCurrency(request);

        // Then
        assertNotNull(response);
        assertEquals(sourceCurrency, response.getSourceCurrency());
        assertEquals(targetCurrency, response.getTargetCurrency());
        assertEquals(sourceAmount, response.getSourceAmount());
        assertEquals(0, expectedTargetAmount.compareTo(response.getTargetAmount()));
        assertEquals(exchangeRate, response.getExchangeRate());
        assertNotNull(response.getTransactionId());
        assertNotNull(response.getTimestamp());

        ArgumentCaptor<ConversionEntity> entityCaptor = ArgumentCaptor.forClass(ConversionEntity.class);
        verify(conversionRepository).save(entityCaptor.capture());
        
        ConversionEntity savedEntity = entityCaptor.getValue();
        assertEquals(sourceCurrency, savedEntity.getSourceCurrency());
        assertEquals(targetCurrency, savedEntity.getTargetCurrency());
        assertEquals(sourceAmount, savedEntity.getSourceAmount());
        assertEquals(0, expectedTargetAmount.compareTo(savedEntity.getTargetAmount()));
        assertEquals(exchangeRate, savedEntity.getExchangeRate());
    }

    @Test
    void findByTransactionId_Success() {
        // Given
        String transactionId = "test-transaction-id";
        Pageable pageable = PageRequest.of(0, 10);
        
        LocalDateTime now = LocalDateTime.now();
        ConversionEntity entity = createTestConversionEntity(transactionId, now);
        
        Page<ConversionEntity> page = new PageImpl<>(List.of(entity));
        
        when(conversionRepository.findByTransactionId(transactionId, pageable)).thenReturn(page);
        
        // When
        Page<ConversionResponse> result = conversionService.findByTransactionId(transactionId, pageable);
        
        // Then
        assertEquals(1, result.getTotalElements());
        ConversionResponse response = result.getContent().getFirst();
        assertEquals(transactionId, response.getTransactionId());
        assertEquals("USD", response.getSourceCurrency());
        assertEquals("EUR", response.getTargetCurrency());
        assertEquals(0, new BigDecimal("100.00").compareTo(response.getSourceAmount()));
        assertEquals(0, new BigDecimal("85.00").compareTo(response.getTargetAmount()));
        assertEquals(0, new BigDecimal("0.85").compareTo(response.getExchangeRate()));
        assertEquals(now, response.getTimestamp());
    }

    @Test
    void findByDateRange_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        
        ConversionEntity entity = createTestConversionEntity();
        
        Page<ConversionEntity> page = new PageImpl<>(List.of(entity));
        
        when(conversionRepository.findByTimestampBetween(startDate, endDate, pageable)).thenReturn(page);
        
        // When
        Page<ConversionResponse> result = conversionService.findByDateRange(startDate, endDate, pageable);
        
        // Then
        assertEquals(1, result.getTotalElements());
        verify(conversionRepository).findByTimestampBetween(startDate, endDate, pageable);
    }

    private ConversionEntity createTestConversionEntity() {
        return createTestConversionEntity("test-transaction-id", LocalDateTime.now());
    }

    private ConversionEntity createTestConversionEntity(String transactionId, LocalDateTime timestamp) {
        return ConversionEntity.builder()
            .transactionId(transactionId)
            .sourceAmount(new BigDecimal("100.00"))
            .sourceCurrency("USD")
            .targetAmount(new BigDecimal("85.00"))
            .targetCurrency("EUR")
            .exchangeRate(new BigDecimal("0.85"))
            .timestamp(timestamp)
            .build();
    }

}
