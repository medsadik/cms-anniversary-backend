package com.example.cms_anniversary_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Scheduling is enabled via @EnableScheduling
    // Cron expressions are configured in application.yml
}