package com.forex.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {
    
    @NotEmpty(message = "Source currency is required")
    private String sourceCurrency;
    
    @NotEmpty(message = "Target currency is required")
    private String targetCurrency;
}
