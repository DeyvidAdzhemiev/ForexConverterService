package com.forex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "com.forex",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com.forex.exception.*"
    )
)
public class ForexConverterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForexConverterServiceApplication.class, args);
    }

}
