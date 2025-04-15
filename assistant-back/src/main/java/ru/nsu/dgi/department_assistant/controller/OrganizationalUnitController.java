package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.OrganizationalUnitService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizational-units")
@RequiredArgsConstructor
@Tag(name = "Organizational unit", description = "A controller for CRUD-operations with organizational units")
public class OrganizationalUnitController {
    private final OrganizationalUnitService organizationalUnitService;

    @GetMapping
    public ResponseEntity<List<OrganizationalUnitResponseDto>> getAllOrganizationalUnits() {
        return ResponseEntity.ok(organizationalUnitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationalUnitResponseDto> getOrganizationalUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationalUnitService.getById(id));
    }

    @PostMapping
    public ResponseEntity<OrganizationalUnitResponseDto> createOrganizationalUnit(@RequestBody OrganizationalUnitRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationalUnitService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationalUnitResponseDto> updateOrganizationalUnit(@PathVariable Long id, @RequestBody OrganizationalUnitRequestDto request) {
        return ResponseEntity.ok(organizationalUnitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizationalUnit(@PathVariable Long id) {
        organizationalUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

