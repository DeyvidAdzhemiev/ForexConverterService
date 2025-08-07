package com.forex.controller;

import com.forex.request.ConversionRequest;
import com.forex.response.ConversionResponse;
import com.forex.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionControllerTest {

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private ConversionController conversionController;

    @Test
    void convertCurrency_Success() {
        // Given
        ConversionRequest request = new ConversionRequest(
                new BigDecimal("100.00"),
                "USD",
                "EUR"
        );

        ConversionResponse expectedResponse = createTestConversionResponse();

        when(conversionService.convertCurrency(request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<ConversionResponse> response = conversionController.convertCurrency(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getConversions_ByTransactionId_Success() {
        // Given
        String transactionId = "test-transaction-id";
        
        ConversionResponse conversionResponse = createTestConversionResponse();
        
        Page<ConversionResponse> expectedPage = new PageImpl<>(List.of(conversionResponse));
        
        when(conversionService.findByTransactionId(eq(transactionId), any(Pageable.class)))
                .thenReturn(expectedPage);

        // When
        ResponseEntity<Page<ConversionResponse>> response = conversionController.getConversions(
                transactionId, null, null, Pageable.unpaged());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(conversionResponse, response.getBody().getContent().getFirst());
    }

    @Test
    void getConversions_ByDateRange_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        ConversionResponse conversionResponse = createTestConversionResponse();
        
        Page<ConversionResponse> expectedPage = new PageImpl<>(List.of(conversionResponse));
        
        when(conversionService.findByDateRange(eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(expectedPage);

        // When
        ResponseEntity<Page<ConversionResponse>> response = conversionController.getConversions(
                null, startDate, endDate, Pageable.unpaged());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(conversionResponse, response.getBody().getContent().get(0));
    }

    private ConversionResponse createTestConversionResponse() {
        return new ConversionResponse(
            "test-transaction-id",
            new BigDecimal("100.00"),
            "USD",
            new BigDecimal("85.00"),
            "EUR",
            new BigDecimal("0.85"),
            LocalDateTime.now()
        );
    }
}
