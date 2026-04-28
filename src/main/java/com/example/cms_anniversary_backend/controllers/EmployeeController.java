package com.example.cms_anniversary_backend.controllers;


import com.example.cms_anniversary_backend.dtos.ApiResponse;
import com.example.cms_anniversary_backend.dtos.EmployeeDTO;
import com.example.cms_anniversary_backend.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees(
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        log.info("GET /api/employees - activeOnly: {}", activeOnly);

        List<EmployeeDTO> employees = activeOnly
                ? employeeService.getAllActiveEmployees()
                : employeeService.getAllEmployees();

        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long id) {
        log.info("GET /api/employees/{}", id);

        return employeeService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(ApiResponse.success(employee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeByEmail(@PathVariable String email) {
        log.info("GET /api/employees/email/{}", email);

        return employeeService.getEmployeeByEmail(email)
                .map(employee -> ResponseEntity.ok(ApiResponse.success(employee)))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/{email}/cc-list")
    public ResponseEntity<ApiResponse<EmployeeDTO>> addCcList(
            @PathVariable String email,
            @RequestBody List<String> ccEmails) {
        log.info("POST /api/employees/{}/cc-list - Adding {} CC emails", email, ccEmails.size());

        try {
            EmployeeDTO updated = employeeService.addCcListByEmail(email, ccEmails);
            return ResponseEntity.ok(ApiResponse.success("CC list added successfully", updated));
        } catch (IllegalArgumentException e) {
            log.error("Failed to add CC list", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countActiveEmployees() {
        log.info("GET /api/employees/count");

        long count = employeeService.countActiveEmployees();
        return ResponseEntity.ok(ApiResponse.success("Active employees count", count));
    }

    @GetMapping("/birthdays-this-week")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getBirthdaysThisWeek() {
        List<EmployeeDTO> employees = employeeService.getBirthdaysThisWeek();
        return ResponseEntity.ok(ApiResponse.success("This Week Birthdays", employees));

    }
    @GetMapping("/anniversaries-this-week")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getWorkAnniversaryThisWeek() {
        List<EmployeeDTO> employees = employeeService.getWorkAnniversaryThisWeek();
        return ResponseEntity.ok(ApiResponse.success("This Week Work Anniversary", employees));
    }

}
