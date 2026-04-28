package com.example.cms_anniversary_backend.mappers;


import com.example.cms_anniversary_backend.dtos.EmployeeDTO;
import com.example.cms_anniversary_backend.entities.Employee;
import org.mapstruct.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", expression = "java(getManagerName(employee))")
    @Mapping(target = "ccListEmails", expression = "java(getCcListEmails(employee))")
    EmployeeDTO toDTO(Employee employee);

    List<EmployeeDTO> toDTOList(List<Employee> employees);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "ccList", ignore = true)
    Employee toEntity(EmployeeDTO dto);

    default String getManagerName(Employee employee) {
        if (employee.getManager() != null) {
            return employee.getManager().getFirstName() + " " + employee.getManager().getLastName();
        }
        return null;
    }

    default Set<String> getCcListEmails(Employee employee) {
        if (employee.getCcList() != null && !employee.getCcList().isEmpty()) {
            return employee.getCcList().stream()
                    .map(Employee::getEmail)
                    .collect(Collectors.toSet());
        }
        return null;
    }
}
