package com.forex.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionEntity {
    
    @Id
    private String transactionId;
    
    private BigDecimal sourceAmount;

    private String sourceCurrency;

    private BigDecimal targetAmount;

    private String targetCurrency;

    private BigDecimal exchangeRate;

    private LocalDateTime timestamp;
}
