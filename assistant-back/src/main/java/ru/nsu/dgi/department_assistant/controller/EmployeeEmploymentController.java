package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeEmploymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employments")
public class EmployeeEmploymentController {
    private final EmployeeEmploymentService employeeEmploymentService;

    @GetMapping()
    public ResponseEntity<List<EmployeeEmploymentResponseDto>> getAll() {
        return ResponseEntity.ok(employeeEmploymentService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeEmploymentResponseDto>> getEmploymentsByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employeeEmploymentService.getByEmployeeId(employeeId));
    }

    @GetMapping("/id")
    public ResponseEntity<EmployeeEmploymentResponseDto> getEmploymentById(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        return ResponseEntity.ok(employeeEmploymentService.getById(employeeId, postId, employmentTypeId));
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeEmploymentResponseDto> createEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeEmploymentService.create(employeeId, postId, employmentTypeId));
    }

    @PutMapping("/update")
    public ResponseEntity<EmployeeEmploymentResponseDto> updateEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId,
            @RequestBody EmployeeEmploymentRequestDto dto
    ) {
        return ResponseEntity.ok(employeeEmploymentService.update(employeeId, postId, employmentTypeId, dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        employeeEmploymentService.delete(employeeId, postId, employmentTypeId);
        return ResponseEntity.noContent().build();
    }
}
