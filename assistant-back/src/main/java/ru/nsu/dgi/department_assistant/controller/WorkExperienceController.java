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
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.WorkExperienceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/work-experience")
public class WorkExperienceController {
    private final WorkExperienceService workExperienceService;

    @GetMapping()
    public ResponseEntity<List<WorkExperienceResponseDto>> getAll() {
        return ResponseEntity.ok(workExperienceService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<WorkExperienceResponseDto> getPassportInfoByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(workExperienceService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<WorkExperienceResponseDto> createWorkExperience(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody WorkExperienceRequestDto workExperienceRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workExperienceService.create(employeeId, workExperienceRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<WorkExperienceResponseDto> updateWorkExperience(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody WorkExperienceRequestDto workExperienceRequestDto
    ) {
        return ResponseEntity.ok(workExperienceService.update(employeeId, workExperienceRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteWorkExperience(
            @RequestParam("employeeId") UUID employeeId
    ) {
        workExperienceService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
