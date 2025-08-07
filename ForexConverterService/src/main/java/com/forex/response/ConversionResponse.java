package com.forex.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResponse {

    private String transactionId;

    private BigDecimal sourceAmount;

    private String sourceCurrency;

    private BigDecimal targetAmount;

    private String targetCurrency;

    private BigDecimal exchangeRate;

    private LocalDateTime timestamp;
}
