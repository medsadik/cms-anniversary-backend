package com.example.cms_anniversary_backend.dtos;

import com.example.cms_anniversary_backend.entities.EmailStatus;
import com.example.cms_anniversary_backend.entities.EmailType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLogDTO {
    private Long id;
    private String recipientEmail;
    private String subject;
    private String body;
    private EmailType type;
    private String ccList;
    private LocalDateTime sentAt;
    private EmailStatus status;
    private String errorMessage;
}