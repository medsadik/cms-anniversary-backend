package com.example.cms_anniversary_backend.services;



import com.example.cms_anniversary_backend.dtos.EmployeeDTO;
import com.example.cms_anniversary_backend.dtos.WeeklyStatsDTO;
import com.example.cms_anniversary_backend.entities.Employee;
import com.example.cms_anniversary_backend.mappers.EmployeeMapper;
import com.example.cms_anniversary_backend.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeDTO> getAllActiveEmployees() {
        log.debug("Fetching all active employees");
        List<Employee> employees = employeeRepository.findByActiveTrue(Sort.by(Sort.Direction.DESC, "entryDate"));
        return employeeMapper.toDTOList(employees);
    }

    public List<EmployeeDTO> getAllEmployees() {
        log.debug("Fetching all employees sorted by entry date (desc)");
        List<Employee> employees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "entryDate"));
        return employeeMapper.toDTOList(employees);
    }


    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        log.debug("Fetching employee by id: {}", id);
        return employeeRepository.findById(id)
                .map(employeeMapper::toDTO);
    }

    public Optional<EmployeeDTO> getEmployeeByEmail(String email) {
        log.debug("Fetching employee by email: {}", email);
        return employeeRepository.findByEmail(email)
                .map(employeeMapper::toDTO);
    }

    public long countActiveEmployees() {
        return employeeRepository.countActiveEmployees();
    }

    public long countBirthdaysOfTheWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        long count = employeeRepository.countBirthdaysOfTheWeek(today);
        log.info("There are {} employee birthdays this week", count);
        return count;
    }
    public long countWorkAnniversaryOfTheWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        long count = employeeRepository.countWorkAnniversaryOfTheWeek(today);
        log.info("There are {} employee birthdays this week", count);
        return count;
    }

    @Transactional
    public EmployeeDTO addCcListByEmail(String employeeEmail, List<String> ccEmails) {
        log.info("Adding CC list to employee: {}", employeeEmail);

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + employeeEmail));

        Set<Employee> ccList = ccEmails.stream()
                .map(email -> employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("CC employee not found with email: " + email)))
                .collect(Collectors.toSet());

        employee.getCcList().addAll(ccList);
        Employee updated = employeeRepository.save(employee);

        log.info("Added {}   employees to CC list of {}", ccList.size(), employeeEmail);
        return employeeMapper.toDTO(updated);
    }

    public List<EmployeeDTO> getEmployeeWithBirthdayToday() {
        List<Employee> employeesWithBirthdayOn = employeeRepository.findEmployeesWithBirthdayOn(LocalDate.now());
        return employeeMapper.toDTOList(employeesWithBirthdayOn);

    }

    public List<EmployeeDTO> getBirthdaysThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<Employee> employees = employeeRepository.findEmployeesWithBirthdayThisWeek(startOfWeek, endOfWeek);

        // Map to DTOs — assuming you already have a mapper
        return employees.stream()
                .map(this::mapToDto)
                .toList();
    }
    public List<EmployeeDTO> getWorkAnniversaryThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<Employee> employees = employeeRepository.findEmployeesWithWorkAnniversaryThisWeek(startOfWeek, endOfWeek);

        // Map to DTOs — assuming you already have a mapper
        return employees.stream()
                .map(this::mapToDto)
                .toList();
    }

    private EmployeeDTO mapToDto(Employee e) {
        return EmployeeDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .birthDate(e.getBirthDate())
                .entryDate(e.getEntryDate())
                .active(e.getActive())
                .matricule(e.getMatricule())
                .build();
    }
}
