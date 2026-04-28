package com.example.cms_anniversary_backend.controllers;

import com.example.cms_anniversary_backend.dtos.ApiResponse;
import com.example.cms_anniversary_backend.dtos.EmailLogDTO;
import com.example.cms_anniversary_backend.dtos.EmailLogFilterDTO;
import com.example.cms_anniversary_backend.entities.EmailStatus;
import com.example.cms_anniversary_backend.entities.EmailType;
import com.example.cms_anniversary_backend.services.EmailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/email-logs")
@RequiredArgsConstructor
@Slf4j
public class EmailLogController {

    private final EmailLogService logService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getLogs() {
        log.info("GET /api/email-logs");
        List<EmailLogDTO> allLogs = logService.getAllLogs();
        return ResponseEntity.ok(ApiResponse.success(allLogs));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getAllLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) EmailType type,
            @RequestParam(required = false) EmailStatus status) {
        log.info("GET /api/email-logs - startDate: {}, endDate: {}, type: {}, status: {}",
                startDate, endDate, type, status);



        EmailLogFilterDTO filter = EmailLogFilterDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .status(status)
                .build();

        List<EmailLogDTO> logs = logService.getLogsByFilters(filter);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailLogDTO>> getLogById(@PathVariable Long id) {
        log.info("GET /api/email-logs/{}", id);

        return logService.getLogById(id)
                .map(log -> ResponseEntity.ok(ApiResponse.success(log)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/recipient/{email}")
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getLogsByRecipient(@PathVariable String email) {
        log.info("GET /api/email-logs/recipient/{}", email);

        List<EmailLogDTO> logs = logService.getLogsByRecipient(email);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getLogsByType(@PathVariable EmailType type) {
        log.info("GET /api/email-logs/type/{}", type);

        List<EmailLogDTO> logs = logService.getLogsByType(type);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getLogsByStatus(@PathVariable EmailStatus status) {
        log.info("GET /api/email-logs/status/{}", status);

        List<EmailLogDTO> logs = logService.getLogsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/stats/count")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@RequestParam EmailStatus status) {
        log.info("GET /api/email-logs/stats/count - status: {}", status);

        long count = logService.countByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Email count by status", count));
    }
    @GetMapping("/sent-today")
    public ResponseEntity<ApiResponse<List<EmailLogDTO>>> getEmailsSentToday() {
        log.info("GET /api/email-logs/today");

        List<EmailLogDTO> logs = logService.getEmailsSentToday();

        return ResponseEntity.ok(ApiResponse.success("Emails sent today", logs));
    }
}
