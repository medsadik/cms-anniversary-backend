package com.example.cms_anniversary_backend.controllers;



import com.example.cms_anniversary_backend.dtos.ApiResponse;
import com.example.cms_anniversary_backend.dtos.SyncResponseDTO;
import com.example.cms_anniversary_backend.services.EmployeeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    private final EmployeeSyncService syncService;

    @PostMapping
    public ResponseEntity<ApiResponse<SyncResponseDTO>> triggerSync() {
        log.info("POST /api/sync - Manual sync triggered");

        try {
            SyncResponseDTO response = syncService.syncEmployees();

            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Sync completed successfully", response));
            } else {
                return ResponseEntity.status(500)
                        .body(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            log.error("Sync failed with exception", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Sync failed: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> getSyncStatus() {
        log.info("GET /api/sync/status");

        return ResponseEntity.ok(ApiResponse.success("Sync service is running"));
    }
}