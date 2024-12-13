package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @GetMapping("/info")
    public ResponseEntity<List<EmployeeWithAllInfoResponseDto>> getAllEmployeesWithInfo() {
        return ResponseEntity.ok(employeeService.getAllEmployeeWithAllInfos());
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<EmployeeWithAllInfoResponseDto> getEmployeeWithInfoById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeWithAllInfos(id));
    }
}
