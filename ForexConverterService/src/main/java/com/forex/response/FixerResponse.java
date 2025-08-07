package com.forex.response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerResponse {

    private boolean success;

    private long timestamp;

    private String base;

    private String date;

    private Map<String, Double> rates;

    private FixerErrorResponse error;
}


