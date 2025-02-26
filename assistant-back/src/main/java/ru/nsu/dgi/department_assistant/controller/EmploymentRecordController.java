
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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentRecordService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employment-records")
public class EmploymentRecordController {
    private final EmploymentRecordService employmentRecordService;

    @GetMapping()
    public ResponseEntity<List<EmploymentRecordResponseDto>> getAllEmploymentRecords() {
        return ResponseEntity.ok(employmentRecordService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<EmploymentRecordResponseDto> getByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employmentRecordService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<EmploymentRecordResponseDto> createEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentRecordRequestDto employmentRecordRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentRecordService.create(employeeId, employmentRecordRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<EmploymentRecordResponseDto> updateEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentRecordRequestDto employmentRecordRequestDto
    ) {
        return ResponseEntity.ok(employmentRecordService.update(employeeId, employmentRecordRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId
    ) {
        employmentRecordService.deleteByEmployeeId(employeeId);
        return ResponseEntity.noContent().build();
    }
}
