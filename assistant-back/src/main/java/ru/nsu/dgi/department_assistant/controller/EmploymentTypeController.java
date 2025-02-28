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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentTypeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/employment-type")
public class EmploymentTypeController {
    private final EmploymentTypeService employmentTypeService;

    @GetMapping()
    public ResponseEntity<List<EmploymentTypeResponseDto>> getAll() {
        return ResponseEntity.ok(employmentTypeService.getAll());
    }

    @GetMapping("/id")
    public ResponseEntity<EmploymentTypeResponseDto> getById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(employmentTypeService.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<EmploymentTypeResponseDto> create(
            @RequestBody EmploymentTypeRequestDto employmentTypeRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentTypeService.create(employmentTypeRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<EmploymentTypeResponseDto> update(
            @RequestParam("id") Integer id,
            @RequestBody EmploymentTypeRequestDto employmentTypeRequestDto
    ) {
        return ResponseEntity.ok(employmentTypeService.update(id, employmentTypeRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("id") Integer id) {
        employmentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
