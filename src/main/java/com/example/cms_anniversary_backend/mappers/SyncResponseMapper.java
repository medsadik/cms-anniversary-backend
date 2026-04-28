package com.example.cms_anniversary_backend.mappers;


import com.example.cms_anniversary_backend.dtos.SyncResponseDTO;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface SyncResponseMapper {

    @Mapping(target = "syncTimestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "totalProcessed", expression = "java(employeesAdded + employeesUpdated + employeesDeactivated)")
    SyncResponseDTO buildResponse(
            int employeesAdded,
            int employeesUpdated,
            int employeesDeactivated,
            String message,
            boolean success
    );

    default SyncResponseDTO buildSuccessResponse(int added, int updated, int deactivated) {
        String message = String.format(
                "Sync completed successfully: %d added, %d updated, %d deactivated",
                added, updated, deactivated
        );

        return SyncResponseDTO.builder()
                .employeesAdded(added)
                .employeesUpdated(updated)
                .employeesDeactivated(deactivated)
                .totalProcessed(added + updated + deactivated)
                .message(message)
                .syncTimestamp(LocalDateTime.now())
                .success(true)
                .build();
    }

    default SyncResponseDTO buildErrorResponse(String errorMessage) {
        return SyncResponseDTO.builder()
                .employeesAdded(0)
                .employeesUpdated(0)
                .employeesDeactivated(0)
                .totalProcessed(0)
                .message("Sync failed: " + errorMessage)
                .syncTimestamp(LocalDateTime.now())
                .success(false)
                .build();
    }
}