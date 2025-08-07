package com.forex.service.impl;

import com.forex.entity.ConversionEntity;
import com.forex.exception.ConversionException;
import com.forex.request.ConversionRequest;
import com.forex.response.ConversionResponse;
import com.forex.response.ExchangeRateResponse;
import com.forex.repository.ConversionRepository;
import com.forex.service.ConversionService;
import com.forex.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionServiceImpl implements ConversionService {

    private final ExchangeRateService exchangeRateService;
    private final ConversionRepository conversionRepository;

    @Override
    public ConversionResponse convertCurrency(ConversionRequest request) {
        log.info("Converting {} {} to {}", request.getSourceAmount(), request.getSourceCurrency(), request.getTargetCurrency());

        try {
            ExchangeRateResponse rateResponse = exchangeRateService.getExchangeRate(
                    request.getSourceCurrency(),
                    request.getTargetCurrency())
                .orElseThrow(() -> new ConversionException("Failed to get exchange rate"));

            BigDecimal targetAmount = request.getSourceAmount()
                    .multiply(rateResponse.getExchangeRate())
                    .setScale(2, RoundingMode.HALF_UP);

            String transactionId = UUID.randomUUID().toString();
            LocalDateTime timestamp = LocalDateTime.now();

            ConversionEntity entity = ConversionEntity.builder()
                    .transactionId(transactionId)
                    .sourceAmount(request.getSourceAmount())
                    .sourceCurrency(request.getSourceCurrency())
                    .targetAmount(targetAmount)
                    .targetCurrency(request.getTargetCurrency())
                    .exchangeRate(rateResponse.getExchangeRate())
                    .timestamp(timestamp)
                    .build();

            conversionRepository.save(entity);

            return mapEntityToResponse().apply(entity);
        } catch (Exception e) {
            log.error("Error converting currency: {}", e.getMessage());
            throw new ConversionException("Failed to convert currency: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Page<ConversionResponse> findByTransactionId(String transactionId, Pageable pageable) {
        log.info("Finding conversions by transaction ID: {}", transactionId);
        
        return conversionRepository.findByTransactionId(transactionId, pageable)
                .map(mapEntityToResponse());
    }
    
    @Override
    public Page<ConversionResponse> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Finding conversions between {} and {}", startDate, endDate);
        
        return conversionRepository.findByTimestampBetween(startDate, endDate, pageable)
                .map(mapEntityToResponse());
    }
    
    private Function<ConversionEntity, ConversionResponse> mapEntityToResponse() {
        return entity -> new ConversionResponse(
                entity.getTransactionId(),
                entity.getSourceAmount(),
                entity.getSourceCurrency(),
                entity.getTargetAmount(),
                entity.getTargetCurrency(),
                entity.getExchangeRate(),
                entity.getTimestamp()
        );
    }
}
