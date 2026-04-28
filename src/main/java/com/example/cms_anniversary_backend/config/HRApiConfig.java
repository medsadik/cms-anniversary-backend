package com.example.cms_anniversary_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hr.api")
@Data
public class HRApiConfig {
    private String baseUrl;
    private String apiKey;
    private String employeesEndpoint;
    private int connectionTimeout;
    private int readTimeout;
}