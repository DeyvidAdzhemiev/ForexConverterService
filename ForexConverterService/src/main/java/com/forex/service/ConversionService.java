package com.forex.service;

import com.forex.request.ConversionRequest;
import com.forex.response.ConversionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ConversionService {

    ConversionResponse convertCurrency(ConversionRequest request);

    Page<ConversionResponse> findByTransactionId(String transactionId, Pageable pageable);

    Page<ConversionResponse> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
