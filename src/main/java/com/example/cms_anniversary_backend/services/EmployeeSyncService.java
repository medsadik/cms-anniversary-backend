package com.example.cms_anniversary_backend.services;

import com.example.cms_anniversary_backend.config.HRApiConfig;
import com.example.cms_anniversary_backend.dtos.EmployeeDTO;
import com.example.cms_anniversary_backend.dtos.SyncResponseDTO;
import com.example.cms_anniversary_backend.entities.Employee;
import com.example.cms_anniversary_backend.mappers.SyncResponseMapper;
import com.example.cms_anniversary_backend.repositories.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeSyncService {

    private final RestTemplate restTemplate;
    private final HRApiConfig hrApiConfig;
    private final EmployeeRepository employeeRepository;
    private final SyncResponseMapper syncResponseMapper;

    @Transactional
    public SyncResponseDTO syncEmployees() {
        log.info("Starting employee synchronization from HR API");

        try {
            List<EmployeeDTO> hrEmployees = fetchEmployeesFromHR();

            if (hrEmployees == null || hrEmployees.isEmpty()) {
                log.warn("No employees received from HR API");
                return syncResponseMapper.buildErrorResponse("No employees received from HR API");
            }

            int added = 0;
            int updated = 0;
            int deactivated = 0;

            Set<String> hrExternalIds = hrEmployees.stream()
                    .map(EmployeeDTO::getMatricule)
                    .collect(Collectors.toSet());

            // First pass: Create/update employees without relationships
            Map<String, Employee> employeeMap = new HashMap<>();

            for (EmployeeDTO hrDto : hrEmployees) {
                Optional<Employee> existingOpt = employeeRepository.findByMatricule(hrDto.getMatricule());

                Employee employee;
                if (existingOpt.isPresent()) {
                    employee = existingOpt.get();
                    updateEmployeeBasicInfo(employee, hrDto);
                    updated++;
                } else {
                    employee = createEmployeeFromHRDTO(hrDto);
                    added++;
                }

                employee.setActive(true);
                employee = employeeRepository.save(employee);
                employeeMap.put(employee.getMatricule(), employee);
            }

            // Second pass: Add manager to employee's ccList from ResponsableDirect
            for (EmployeeDTO hrDto : hrEmployees) {
                if (hrDto.getManagerMatricule() == null) continue;

                Employee employee = employeeMap.get(hrDto.getMatricule());
                Employee manager = employeeMap.get(hrDto.getManagerMatricule());

                if (employee != null && manager != null) {
                    employee.setManager(manager);
                    employee.getCcList().add(manager);
                    employeeRepository.save(employee);
                } else {
                    log.warn("Could not resolve manager '{}' for employee '{}'",
                            hrDto.getManagerMatricule(), hrDto.getMatricule());
                }
            }

            // Deactivate employees not in HR system
            List<Employee> allActiveEmployees = employeeRepository.findByActiveTrue();
            for (Employee employee : allActiveEmployees) {
                if (employee.getMatricule() != null && !hrExternalIds.contains(employee.getMatricule())) {
                    employee.setActive(false);
                    employeeRepository.save(employee);
                    deactivated++;
                }
            }

            log.info("Sync completed: {} added, {} updated, {} deactivated", added, updated, deactivated);
            return syncResponseMapper.buildSuccessResponse(added, updated, deactivated);

        } catch (Exception e) {
            log.error("Error during employee synchronization", e);
            return syncResponseMapper.buildErrorResponse(e.getMessage());
        }
    }

    @Scheduled(cron = "${scheduling.sync.cron}")
    public void scheduledSync() {
        log.info("Scheduled employee sync triggered");
        syncEmployees();
    }

    private List<EmployeeDTO> fetchEmployeesFromHR() {
        String employeesUrl = hrApiConfig.getBaseUrl() + hrApiConfig.getEmployeesEndpoint();
//        String postesUrl = hrApiConfig.getBaseUrl() + hrApiConfig.getPostesEndpoint();

        try {
            String employeesResponse = restTemplate.getForObject(employeesUrl, String.class);
//            String postesResponse = restTemplate.getForObject(postesUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> postIdToNameMap = new HashMap<>();

//            JsonNode postesPayload = mapper.readTree(postesResponse).path("payload");
//            for (JsonNode postNode : postesPayload) {
//                String postId = postNode.path("Fonction").path("id").asText();
//                String postName = postNode.path("Fonction").path("intitule_du_poste").asText();
//                postIdToNameMap.put(postId, postName);
//            }

            JsonNode employeesPayload = mapper.readTree(employeesResponse).path("payload");
            List<EmployeeDTO> employeeList = new ArrayList<>();

            for (JsonNode node : employeesPayload) {
                JsonNode employeeNode = node.path("Employe");
                if (employeeNode.path("matricule").asLong() == 0) continue;

                String firstName = employeeNode.path("prenom").asText();
                String lastName = employeeNode.path("nom").asText();
                String email = employeeNode.path("poste_email").asText();
                String matricule = employeeNode.path("matricule").asText();
                String birthDateStr = employeeNode.path("datenaissance").asText(null);
                String civilite = employeeNode.path("civilite").asText(null);
                String managerMatricule = node.path("ResponsableDirect").path("matricule").asText(null);
                if (managerMatricule != null && managerMatricule.isEmpty()) managerMatricule = null;

                LocalDate birthDate = null;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                if (birthDateStr != null && !birthDateStr.isEmpty()) {
                    try {
                        birthDate = LocalDate.parse(birthDateStr, formatter);
                    } catch (DateTimeParseException e) {
                        log.warn("Could not parse birth date '{}' for matricule {}", birthDateStr, matricule);
                    }
                }
                String entryDateStr = employeeNode.path("dateentree").asText(null);
                LocalDate entryDate = null;
                if (entryDateStr != null && !entryDateStr.isEmpty()) {
                    try {
                        entryDate = LocalDate.parse(entryDateStr, formatter);
                    } catch (DateTimeParseException e) {
                        log.warn("Could not parse entry date '{}' for matricule {}", entryDateStr, matricule);
                    }
                }
                EmployeeDTO dto = EmployeeDTO.builder()
                        .birthDate(birthDate)
                        .entryDate(entryDate)
                        .matricule(matricule)
                        .civilite(civilite)
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .active(true)
                        .managerMatricule(managerMatricule)
                        .build();

                employeeList.add(dto);
            }

            return employeeList;
        } catch (Exception e) {
            log.error("Failed to fetch employees from HR API", e);
            throw new RuntimeException("Failed to fetch employees from HR API: " + e.getMessage(), e);
        }
    }

    private Employee createEmployeeFromHRDTO(EmployeeDTO hrDto) {
        return Employee.builder()
                .matricule(hrDto.getMatricule())
                .firstName(hrDto.getFirstName())
                .lastName(hrDto.getLastName())
                .email(hrDto.getEmail())
                .birthDate(hrDto.getBirthDate())
                .entryDate(hrDto.getEntryDate())
                .active(true)
                .build();
    }

    private void updateEmployeeBasicInfo(Employee employee, EmployeeDTO hrDto) {
        employee.setFirstName(hrDto.getFirstName());
        employee.setLastName(hrDto.getLastName());
        employee.setEmail(hrDto.getEmail());
        employee.setBirthDate(hrDto.getBirthDate());
        employee.setEntryDate(hrDto.getEntryDate());
    }
}
