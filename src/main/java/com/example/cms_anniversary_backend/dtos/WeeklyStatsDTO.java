package com.example.cms_anniversary_backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyStatsDTO {

    private long totalEmployees;
    private long birthdaysThisWeek;
    private long workAnniversariesThisWeek;
    private long emailsSentToday;
}