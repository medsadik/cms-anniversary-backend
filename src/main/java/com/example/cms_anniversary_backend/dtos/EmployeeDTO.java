package com.example.cms_anniversary_backend.dtos;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDate entryDate;
    private Long managerId;
    private String managerName;
    private String civilite;
    private Set<String> ccListEmails;
    private Boolean active;
    private String matricule;
    private String managerMatricule;
}