package com.forex.controller;

import com.forex.request.ConversionRequest;
import com.forex.response.ConversionResponse;
import com.forex.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/conversions")
@RequiredArgsConstructor
@Tag(name = "Conversion API", description = "Operations for currency conversion and retrieving conversion history")
public class ConversionController {

    private final ConversionService conversionService;
    private static final Logger log = LoggerFactory.getLogger(ConversionController.class);
    
    @PostMapping
    @Operation(summary = "Convert currency", description = "Converts an amount from source currency to target currency")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully converted currency", 
                    content = @Content(schema = @Schema(implementation = ConversionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error or exchange rate provider error")
    })
    public ResponseEntity<ConversionResponse> convertCurrency(@Valid @RequestBody ConversionRequest request) {
        return ResponseEntity.ok(conversionService.convertCurrency(request));
    }
    
    @GetMapping
    @Operation(summary = "Get conversion history", description = "Returns a paginated list of conversions filtered by transaction ID or date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved conversions", 
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ConversionResponse>> getConversions(
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss[.SSSSSS]") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss[.SSSSSS]") LocalDateTime endDate,
            Pageable pageable) {
        
        log.debug("Request parameters - transactionId: {}, startDate: {}, endDate: {}, pageable: {}", 
                 transactionId, startDate, endDate, pageable);
        
        Page<ConversionResponse> conversions;
        
        if (transactionId != null) {

            transactionId = transactionId.trim();
            conversions = conversionService.findByTransactionId(transactionId, pageable);
        } else if (startDate != null && endDate != null) {

            conversions = conversionService.findByDateRange(startDate, endDate, pageable);
        } else {
            throw new IllegalArgumentException("Either transactionId or both startDate and endDate must be provided");
        }
        
        log.debug("Found {} conversions", conversions.getTotalElements());
        return ResponseEntity.ok(conversions);
    }
}
