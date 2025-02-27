package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentStatusService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employment-status")
public class EmploymentStatusController {
    private final EmploymentStatusService employmentStatusService;

    @GetMapping()
    public ResponseEntity<List<EmploymentStatusResponseDto>> getEmploymentStatus() {
        return ResponseEntity.ok(employmentStatusService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<EmploymentStatusResponseDto> getByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employmentStatusService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<EmploymentStatusResponseDto> createEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentStatusRequestDto employmentStatusRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentStatusService.create(employeeId, employmentStatusRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<EmploymentStatusResponseDto> updateEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentStatusRequestDto employmentStatusRequestDto
    ) {
        return ResponseEntity.ok(employmentStatusService.update(employeeId, employmentStatusRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<EmploymentStatusResponseDto> deleteEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId
    ) {
        employmentStatusService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
