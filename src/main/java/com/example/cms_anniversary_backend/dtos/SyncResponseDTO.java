package com.example.cms_anniversary_backend.dtos;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponseDTO {
    private int employeesAdded;
    private int employeesUpdated;
    private int employeesDeactivated;
    private int totalProcessed;
    private String message;
    private LocalDateTime syncTimestamp;
    private boolean success;
}