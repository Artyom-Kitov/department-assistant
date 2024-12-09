package ru.nsu.dgi.department_assistant.controller;

import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDTO;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;
import ru.nsu.dgi.department_assistant.domain.mapper.EmployeeMapper;
import ru.nsu.dgi.department_assistant.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeResponseDTO> employeeResponseDTOs = employees.stream()
                .map(employeeMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(employeeResponseDTOs);
    }
}
