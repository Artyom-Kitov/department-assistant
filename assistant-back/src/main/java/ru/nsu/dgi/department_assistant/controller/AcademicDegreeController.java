package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{id}")
    public ResponseEntity<AcademicDegreeResponseDto> getAcademicDegreeById(@PathVariable int id) {
        return ResponseEntity.ok(academicDegreeService.getById(id));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<AcademicDegreeResponseDto> getAcademicDegreeByEmployeeId(@PathVariable UUID id) {
        return ResponseEntity.ok(academicDegreeService.getByEmployeeId(id));
    }

    @PostMapping()
    public ResponseEntity<AcademicDegreeResponseDto> createAcademicDegree(
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity.ok(academicDegreeService.create(academicDegree));
    }

    @PutMapping()
    public ResponseEntity<AcademicDegreeResponseDto> updateAcademicDegree(
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity.ok(academicDegreeService.update(academicDegree));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcademicDegree(@PathVariable UUID id) {
        academicDegreeService.deleteByEmployeeId(id);
        return ResponseEntity.noContent().build();
    }
}
