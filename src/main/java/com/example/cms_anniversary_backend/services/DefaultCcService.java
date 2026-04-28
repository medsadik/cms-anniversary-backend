package com.example.cms_anniversary_backend.services;

import com.example.cms_anniversary_backend.entities.DefaultCc;
import com.example.cms_anniversary_backend.entities.Employee;
import com.example.cms_anniversary_backend.repositories.DefaultCcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultCcService {

    private final DefaultCcRepository defaultCcRepository;

    public List<String> getDefaultCcEmails() {
        return defaultCcRepository.findAll()
                .stream()
                .map(defaultCc -> defaultCc.getEmployee().getEmail())
                .toList();
    }

    public DefaultCc addDefaultCc(Employee employee) {
        DefaultCc cc = DefaultCc.builder()
                .employee(employee)
                .build();
        return cc;
    }
    public void addAllDefaultCcs(List<Employee> employees) {
        List<DefaultCc> list = employees.stream().map(this::addDefaultCc).toList();
        defaultCcRepository.deleteAll();
        defaultCcRepository.saveAll(list);
    }

    public void removeDefaultCc(Long id) {
        defaultCcRepository.deleteById(id);
    }
}
