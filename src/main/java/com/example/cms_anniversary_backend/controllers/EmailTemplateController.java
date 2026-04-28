package com.example.cms_anniversary_backend.controllers;


import com.example.cms_anniversary_backend.dtos.ApiResponse;
import com.example.cms_anniversary_backend.dtos.EmailTemplateCreateDTO;
import com.example.cms_anniversary_backend.dtos.EmailTemplateDTO;
import com.example.cms_anniversary_backend.dtos.EmailTestDto;
import com.example.cms_anniversary_backend.entities.EmailType;
import com.example.cms_anniversary_backend.services.EmailService;
import com.example.cms_anniversary_backend.services.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateController {

    private final EmailTemplateService templateService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmailTemplateDTO>>> getAllTemplates(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        log.info("GET /api/templates - activeOnly: {}", activeOnly);

        List<EmailTemplateDTO> templates = activeOnly
                ? templateService.getAllActiveTemplates()
                : templateService.getAllTemplates();

        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @PostMapping("/test-email")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody EmailTestDto request) {
        log.info("POST /api/emails/send - templateType={}, email={}, ccList={}",
                request.getTemplateType(), request.getEmail(), request.getCcList());
        emailService.sendTestEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateDTO>> getTemplateById(@PathVariable Long id) {
        log.info("GET /api/templates/{}", id);

        return templateService.getTemplateById(id)
                .map(template -> ResponseEntity.ok(ApiResponse.success(template)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<EmailTemplateDTO>> getTemplateByType(@PathVariable EmailType type) {
        log.info("GET /api/templates/type/{}", type);

        return templateService.getTemplateByType(type)
                .map(template -> ResponseEntity.ok(ApiResponse.success(template)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmailTemplateDTO>> createTemplate(
            @Valid @RequestBody EmailTemplateCreateDTO createDTO) {
        log.info("POST /api/templates - name: {}", createDTO.getName());

        try {
            EmailTemplateDTO created = templateService.createTemplate(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Template created successfully", created));
        } catch (IllegalArgumentException e) {
            log.error("Failed to create template", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmailTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody EmailTemplateCreateDTO updateDTO) {
        log.info("PUT /api/templates/{}", id);

        try {
            EmailTemplateDTO updated = templateService.updateTemplate(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success("Template updated successfully", updated));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update template", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        log.info("DELETE /api/templates/{}", id);

        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok(ApiResponse.success("Template deleted successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete template", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateTemplate(@PathVariable Long id) {
        log.info("PATCH /api/templates/{}/deactivate", id);

        try {
            templateService.deactivateTemplate(id);
            return ResponseEntity.ok(ApiResponse.success("Template deactivated successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Failed to deactivate template", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
