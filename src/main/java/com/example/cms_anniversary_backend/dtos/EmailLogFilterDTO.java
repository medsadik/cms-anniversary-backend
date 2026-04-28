package com.example.cms_anniversary_backend.dtos;



import com.example.cms_anniversary_backend.entities.EmailStatus;
import com.example.cms_anniversary_backend.entities.EmailType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLogFilterDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EmailType type;
    private EmailStatus status;
}
