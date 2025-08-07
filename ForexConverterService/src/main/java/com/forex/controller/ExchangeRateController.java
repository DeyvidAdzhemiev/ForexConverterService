package com.forex.controller;

import com.forex.exception.UnsupportedCurrencyException;
import com.forex.response.ExchangeRateResponse;
import com.forex.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rate API", description = "Operations for retrieving exchange rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateController.class);
    
    @GetMapping
    @Operation(summary = "Get exchange rate for a currency pair", description = "Returns the exchange rate between two currencies. Note: Only EUR is supported as source currency with the free tier of Fixer.io")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rate", 
                    content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or unsupported currency"),
        @ApiResponse(responseCode = "500", description = "Internal server error or exchange rate provider error")
    })
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @RequestParam String sourceCurrency,
            @RequestParam String targetCurrency) {
        
        // Add validation for currency codes
        if (!sourceCurrency.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Source currency must be a valid 3-letter ISO currency code");
        }
        if (!targetCurrency.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Target currency must be a valid 3-letter ISO currency code");
        }
        
        if (!"EUR".equals(sourceCurrency)) {
            throw new UnsupportedCurrencyException(
                "Only EUR is supported as base currency with the free tier of Fixer.io. Please use EUR as the source currency."
            );
        }
        
        try {
            return exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new RuntimeException("Failed to get exchange rate for " + sourceCurrency + "/" + targetCurrency));
        } catch (UnsupportedCurrencyException e) {
            // Let the global exception handler deal with this
            throw e;
        } catch (Exception e) {
            log.error("Error in exchange rate controller: {}", e.getMessage());
            throw e;
        }
    }
}
