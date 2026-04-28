package com.example.cms_anniversary_backend.dtos;


import com.example.cms_anniversary_backend.entities.EmailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplateCreateDTO {

    @NotBlank(message = "Template name is required")
    private String name;

    @NotNull(message = "Email type is required")
    private EmailType type;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;
}
