package com.example.cms_anniversary_backend.services;



import com.example.cms_anniversary_backend.config.EmailConfig;
import com.example.cms_anniversary_backend.dtos.EmailTemplateDTO;
import com.example.cms_anniversary_backend.dtos.EmailTestDto;
import com.example.cms_anniversary_backend.entities.*;
import com.example.cms_anniversary_backend.repositories.EmailLogRepository;
import com.example.cms_anniversary_backend.repositories.EmailTemplateRepository;
import com.example.cms_anniversary_backend.repositories.EmployeeRepository;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;
    private final EmployeeRepository employeeRepository;
    private final EmailTemplateRepository templateRepository;
    private final EmailLogRepository logRepository;
    private final EmailTemplateService emailTemplateService;

//    @Scheduled(cron = "${scheduling.email.cron}")
    @Transactional
    public void sendScheduledAnniversaryEmails() {
        log.info("Starting scheduled anniversary email job");

        LocalDate today = LocalDate.now();

        // Send birthday emails
        List<Employee> birthdayEmployees = employeeRepository.findEmployeesWithBirthdayOn(today);
        log.info("Found {} employees with birthdays today", birthdayEmployees.size());

        for (Employee employee : birthdayEmployees) {
            try {
                sendBirthdayEmail(employee);
            } catch (Exception e) {
                log.error("Failed to send birthday email to {}", employee.getEmail(), e);
            }
        }

        // Send work anniversary emails
        List<Employee> anniversaryEmployees = employeeRepository.findEmployeesWithWorkAnniversaryOn(today);
        log.info("Found {} employees with work anniversaries today", anniversaryEmployees.size());

        for (Employee employee : anniversaryEmployees) {
            try {
                sendWorkAnniversaryEmail(employee);
            } catch (Exception e) {
                log.error("Failed to send work anniversary email to {}", employee.getEmail(), e);
            }
        }

        log.info("Completed anniversary email job");
    }


    public void sendBirthdayEmail(Employee employee) {
        Optional<EmailTemplate> templateOpt = templateRepository.findByTypeAndActiveTrue(EmailType.BIRTHDAY);

        if (templateOpt.isEmpty()) {
            log.warn("No active birthday email template found");
            return;
        }

        EmailTemplate template = templateOpt.get();
        String subject = replacePlaceholders(template.getSubject(), employee);
        String body = replacePlaceholders(template.getBody(), employee);

        sendEmail(employee, subject, body, EmailType.BIRTHDAY);
    }

    public void sendWorkAnniversaryEmail(Employee employee) {
        Optional<EmailTemplate> templateOpt = templateRepository.findByTypeAndActiveTrue(EmailType.WORK_ANNIVERSARY);

        if (templateOpt.isEmpty()) {
            log.warn("No active work anniversary email template found");
            return;
        }

        EmailTemplate template = templateOpt.get();
        int yearsOfService = Period.between(employee.getEntryDate(), LocalDate.now()).getYears();

        String subject = replacePlaceholders(template.getSubject(), employee);
        String body = replacePlaceholders(template.getBody(), employee);

        sendEmail(employee, subject, body, EmailType.WORK_ANNIVERSARY);
    }

    public void sendTestEmail(EmailTestDto emailTestDto){
        // 1️⃣ Fetch the template from DB or predefined set
        EmailTemplateDTO template =  emailTemplateService.getTemplateByType(emailTestDto.getTemplateType()).orElseThrow();
        EmailLog emailLog = EmailLog.builder()
                .recipientEmail(emailTestDto.getEmail())
                .subject(template.getSubject())
                .body(template.getBody())
                .type(emailTestDto.getTemplateType() == EmailType.BIRTHDAY ? EmailType.TEST_BIRTHDAY : EmailType.TEST_WORK_ANNIVERSARY)
                .ccList(String.join(", ", emailTestDto.getCcList()))
                .sentAt(LocalDateTime.now())
                .build();
        // 2️⃣ Prepare message
        try {
            Employee employee = employeeRepository.findByEmail(emailTestDto.getEmail()).orElseThrow();
            String subject = replacePlaceholders(template.getSubject(), employee);
            String body = replacePlaceholders(template.getBody(), employee);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailConfig.getFrom(), emailConfig.getFromName());
            helper.setTo(emailTestDto.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            for (String cc : emailTestDto.getCcList()) {
                helper.addCc(new InternetAddress(cc));
            }
            // 4️⃣ Send email
            mailSender.send(message);
            emailLog.setStatus(EmailStatus.SUCCESS);
            log.info("Test email sent successfully to {} for {}", emailTestDto.getEmail(), emailTestDto.getTemplateType());
        } catch (Exception e) {
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
            log.error("Failed to send test email to {}", emailTestDto.getEmail(), e);
        }
        logRepository.save(emailLog);

}
    private void sendEmail(Employee employee, String subject, String body, EmailType type) {
        List<String> ccEmails = new ArrayList<>();

        if (employee.getManager() != null) {
            ccEmails.add(employee.getManager().getEmail());
        }

        if (employee.getCcList() != null && !employee.getCcList().isEmpty()) {
            ccEmails.addAll(employee.getCcList().stream()
                    .map(Employee::getEmail)
                    .toList());
        }

        EmailLog emailLog = EmailLog.builder()
                .recipientEmail(employee.getEmail())
                .subject(subject)
                .body(body)
                .type(type)
                .ccList(String.join(", ", ccEmails))
                .sentAt(LocalDateTime.now())
                .build();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailConfig.getFrom(), emailConfig.getFromName());
            helper.setTo(employee.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            if (!ccEmails.isEmpty()) {
                helper.setCc(ccEmails.toArray(new String[0]));
            }

            mailSender.send(message);

            emailLog.setStatus(EmailStatus.SUCCESS);
            log.info("Email sent successfully to {} for {}", employee.getEmail(), type);

        } catch (Exception e) {
            emailLog.setStatus(EmailStatus.FAILED);
            emailLog.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}", employee.getEmail(), e);
        }

        logRepository.save(emailLog);
    }




    private String replacePlaceholders(String text, Employee employee) {
        String firstName = employee.getFirstName() != null ? employee.getFirstName() : "";
        String lastName = employee.getLastName() != null ? employee.getLastName() : "";
        String civilite = employee.getCivilite() != null ? employee.getCivilite().trim().toUpperCase() : "";

        // Detect if the employee is female based on civilité
        boolean isFemale = civilite.equals("MME");

        // Replace gender markers like (e)
        String adjustedText = adjustGenderMarkers(text, isFemale);

        return adjustedText
                .replace("{{firstName}}", firstName)
                .replace("{{lastName}}", lastName);
    }


    private String adjustGenderMarkers(String text, boolean isFemale) {
        if (isFemale) {
            // Keep the "(e)" but remove the parentheses → (e) → e
            return text.replaceAll("\\(e\\)", "e");
        } else {
            // Remove "(e)" entirely for males
            return text.replaceAll("\\(e\\)", "");
        }
    }
}

