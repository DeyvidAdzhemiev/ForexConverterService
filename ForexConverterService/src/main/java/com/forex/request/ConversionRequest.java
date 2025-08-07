package com.forex.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRequest {
    
    @NotNull(message = "Source amount is required")
    @Positive(message = "Source amount must be positive")
    private BigDecimal sourceAmount;
    
    @NotEmpty(message = "Source currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Source currency must be a valid 3-letter ISO currency code")
    private String sourceCurrency;
    
    @NotEmpty(message = "Target currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Target currency must be a valid 3-letter ISO currency code")
    private String targetCurrency;
}
