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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.CertificateOfNoCriminalRecordService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/no-criminal-certificate")
public class CertificateOfNoCriminalRecordController {
    private final CertificateOfNoCriminalRecordService certificateService;

    @GetMapping()
    public ResponseEntity<List<CertificateOfNoCriminalRecordResponseDto>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> getCertificateByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(certificateService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> createAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody CertificateOfNoCriminalRecordRequestDto dto
    ) {
        return ResponseEntity.ok(certificateService.create(employeeId, dto));
    }

    @PutMapping("/update")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> updateAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody CertificateOfNoCriminalRecordRequestDto dto
    ) {
        return ResponseEntity.ok(certificateService.update(employeeId, dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAcademicDegree(@RequestParam("id") UUID id) {
        certificateService.deleteByEmployeeId(id);
        return ResponseEntity.noContent().build();
    }
}
