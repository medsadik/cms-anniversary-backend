package com.example.cms_anniversary_backend.services;


import com.example.cms_anniversary_backend.dtos.EmailLogDTO;
import com.example.cms_anniversary_backend.dtos.EmailLogFilterDTO;
import com.example.cms_anniversary_backend.entities.EmailLog;
import com.example.cms_anniversary_backend.entities.EmailStatus;
import com.example.cms_anniversary_backend.entities.EmailType;
import com.example.cms_anniversary_backend.mappers.EmailLogMapper;
import com.example.cms_anniversary_backend.repositories.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailLogService {

    private final EmailLogRepository logRepository;
    private final EmailLogMapper logMapper;

    public List<EmailLogDTO> getAllLogs() {
        log.debug("Fetching all email logs");
        List<EmailLog> logs = logRepository.findAll();
        return logMapper.toDTOList(logs);
    }

    public List<EmailLogDTO> getLogsByFilters(EmailLogFilterDTO filter) {
        log.debug("Fetching email logs with filters: {}", filter);
        List<EmailLog> logs = logRepository.findByFilters(
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getType(),
                filter.getStatus()
        );
        return logMapper.toDTOList(logs);
    }


    public Optional<EmailLogDTO> getLogById(Long id) {
        log.debug("Fetching email log by id: {}", id);
        return logRepository.findById(id)
                .map(logMapper::toDTO);
    }

    public List<EmailLogDTO> getLogsByRecipient(String email) {
        log.debug("Fetching email logs for recipient: {}", email);
        List<EmailLog> logs = logRepository.findByRecipientEmailOrderBySentAtDesc(email);
        return logMapper.toDTOList(logs);
    }

    public List<EmailLogDTO> getLogsByType(EmailType type) {
        log.debug("Fetching email logs by type: {}", type);
        List<EmailLog> logs = logRepository.findByTypeOrderBySentAtDesc(type);
        return logMapper.toDTOList(logs);
    }

    public List<EmailLogDTO> getLogsByStatus(EmailStatus status) {
        log.debug("Fetching email logs by status: {}", status);
        List<EmailLog> logs = logRepository.findByStatusOrderBySentAtDesc(status);
        return logMapper.toDTOList(logs);
    }

    public long countByStatus(EmailStatus status) {
        return logRepository.countByStatus(status);
    }

    public long countByTypeAndDateAfter(EmailType type, LocalDateTime startDate) {
        return logRepository.countByTypeAndSentAtAfter(type, startDate);
    }

    public List<EmailLogDTO> getEmailsSentToday() {
        log.debug("Fetching emails sent today");
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<EmailLog> logs = logRepository.findBySentAtBetween(startOfDay, endOfDay);
        return logMapper.toDTOList(logs);
    }
}