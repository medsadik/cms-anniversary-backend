package com.example.cms_anniversary_backend.controllers;

import com.example.cms_anniversary_backend.entities.Employee;
import com.example.cms_anniversary_backend.repositories.EmployeeRepository;
import com.example.cms_anniversary_backend.services.DefaultCcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/default-cc")
@RequiredArgsConstructor
public class DefaultCcController {

    private final DefaultCcService defaultCcService;
    private final EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<String>> getAllCc() {
        return ResponseEntity.ok(defaultCcService.getDefaultCcEmails());
    }

    @PutMapping()
    public ResponseEntity<Void> addDefaultCcList(@RequestBody List<String> defaultCcEmails) {
        List<Employee> employees = defaultCcEmails.stream().map(email -> employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Employee not found")))
                .toList();
        defaultCcService.addAllDefaultCcs(employees);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCc(@PathVariable Long id) {
        defaultCcService.removeDefaultCc(id);
        return ResponseEntity.noContent().build();
    }
}
