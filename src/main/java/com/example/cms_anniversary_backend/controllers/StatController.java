package com.example.cms_anniversary_backend.controllers;


import com.example.cms_anniversary_backend.dtos.ApiResponse;
import com.example.cms_anniversary_backend.dtos.WeeklyStatsDTO;
import com.example.cms_anniversary_backend.services.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatController {

    public final StatsService statsService;

    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyStatsDTO>> getWeeklyStats() {
        return ResponseEntity.ok(ApiResponse.success("Stats of the week", statsService.getWeeklyStats()));

    }
}
