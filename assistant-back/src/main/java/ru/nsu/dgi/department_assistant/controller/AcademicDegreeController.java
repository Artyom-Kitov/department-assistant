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
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.AcademicDegreeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/academic-degree")
public class AcademicDegreeController {
    private final AcademicDegreeService academicDegreeService;

    @GetMapping()
    public ResponseEntity<List<AcademicDegreeResponseDto>> getAllAcademicDegrees() {
        return ResponseEntity.ok(academicDegreeService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<AcademicDegreeResponseDto> getAcademicDegreeByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(academicDegreeService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<AcademicDegreeResponseDto> createAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(academicDegreeService.create(employeeId, academicDegree));
    }

    @PutMapping("/update")
    public ResponseEntity<AcademicDegreeResponseDto> updateAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity.ok(academicDegreeService.update(employeeId, academicDegree));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAcademicDegree(@RequestParam("employeeId") UUID employeeId) {
        academicDegreeService.deleteByEmployeeId(employeeId);
        return ResponseEntity.noContent().build();
    }
}
