package com.forex.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixerErrorResponse {

    private int code;
    private String info;
}
