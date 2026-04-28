package com.example.cms_anniversary_backend.services;

import com.example.cms_anniversary_backend.dtos.WeeklyStatsDTO;
import org.springframework.stereotype.Service;

@Service
public class StatsService {


    public final EmployeeService employeeService;
    private final EmailLogService emailLogService;

    public StatsService(EmployeeService employeeService, EmailLogService emailLogService) {
        this.employeeService = employeeService;
        this.emailLogService = emailLogService;
    }


    public WeeklyStatsDTO getWeeklyStats() {
        return WeeklyStatsDTO.builder()
                .totalEmployees(employeeService.countActiveEmployees())
                .birthdaysThisWeek(employeeService.countBirthdaysOfTheWeek())
                .workAnniversariesThisWeek(employeeService.countWorkAnniversaryOfTheWeek())
                .emailsSentToday(emailLogService.getEmailsSentToday().size())
                .build();
    }
}
