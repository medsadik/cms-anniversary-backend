package com.example.cms_anniversary_backend.services;



import com.example.cms_anniversary_backend.dtos.EmailTemplateCreateDTO;
import com.example.cms_anniversary_backend.dtos.EmailTemplateDTO;
import com.example.cms_anniversary_backend.entities.EmailTemplate;
import com.example.cms_anniversary_backend.entities.EmailType;
import com.example.cms_anniversary_backend.mappers.EmailTemplateMapper;
import com.example.cms_anniversary_backend.repositories.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailTemplateService {

    private final EmailTemplateRepository templateRepository;
    private final EmailTemplateMapper templateMapper;

    public List<EmailTemplateDTO> getAllTemplates() {
        log.debug("Fetching all email templates");
        List<EmailTemplate> templates = templateRepository.findAll();
        return templateMapper.toDTOList(templates);
    }

    public List<EmailTemplateDTO> getAllActiveTemplates() {
        log.debug("Fetching all active email templates");
        List<EmailTemplate> templates = templateRepository.findByActiveTrue();
        return templateMapper.toDTOList(templates);
    }

    public Optional<EmailTemplateDTO> getTemplateById(Long id) {
        log.debug("Fetching email template by id: {}", id);
        return templateRepository.findById(id)
                .map(templateMapper::toDTO);
    }

    public Optional<EmailTemplateDTO> getTemplateByType(EmailType type) {
        log.debug("Fetching email template by type: {}", type);
        return templateRepository.findByTypeAndActiveTrue(type)
                .map(templateMapper::toDTO);
    }

    @Transactional
    public EmailTemplateDTO createTemplate(EmailTemplateCreateDTO createDTO) {
        log.info("Creating new email template: {}", createDTO.getName());

        if (templateRepository.existsByName(createDTO.getName())) {
            throw new IllegalArgumentException("Template with name '" + createDTO.getName() + "' already exists");
        }

        EmailTemplate template = templateMapper.toEntity(createDTO);
        EmailTemplate saved = templateRepository.save(template);

        log.info("Email template created successfully with id: {}", saved.getId());
        return templateMapper.toDTO(saved);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        log.info("Deleting email template with id: {}", id);

        if (!templateRepository.existsById(id)) {
            throw new IllegalArgumentException("Template not found with id: " + id);
        }

        templateRepository.deleteById(id);
        log.info("Email template deleted successfully: {}", id);
    }

    @Transactional
    public void deactivateTemplate(Long id) {
        log.info("Deactivating email template with id: {}", id);

        EmailTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));

        template.setActive(false);
        templateRepository.save(template);

        log.info("Email template deactivated successfully: {}", id);
    }
    @Transactional
    public EmailTemplateDTO updateTemplate(Long id, EmailTemplateCreateDTO updateDTO) {
        log.info("Updating email template with id: {}", id);

        EmailTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with id: " + id));

        templateMapper.updateEntityFromDTO(updateDTO, template);
        EmailTemplate updated = templateRepository.save(template);

        log.info("Email template updated successfully: {}", id);
        return templateMapper.toDTO(updated);
    }
}
