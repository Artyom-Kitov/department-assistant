package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(
        name = "Certificates of no criminal record",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about certificates of no criminal record."
)
public class CertificateOfNoCriminalRecordController {
    private final CertificateOfNoCriminalRecordService certificateService;

    @Operation(
            summary = "Returns all certificates of no criminal record",
            description = "Returns all certificates of no criminal record according to employees that have " +
                    "this certificate."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<List<CertificateOfNoCriminalRecordResponseDto>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAll());
    }

    @Operation(
            summary = "Returns certificate of no criminal record of an employee",
            description = "Returns a certificate of no criminal record of a certain employee by employee id."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @GetMapping("/employee")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> getCertificateByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(certificateService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new certificate of no criminal record",
            description =
                    "Creates a new certificate of no criminal record for an employee by employee id. " +
                            "Operation may not be possible if a certificate is already exists for this employee."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "412",
                            description = "Entity already exists"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> createAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody CertificateOfNoCriminalRecordRequestDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(certificateService.create(employeeId, dto));
    }

    @Operation(
            summary = "Updates a certificate of no criminal record",
            description =
                    "Updates a certificate of no criminal record for an employee by employee id. " +
                    "Does nothing if there's no certificate for chosen employee."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PutMapping("/update")
    public ResponseEntity<CertificateOfNoCriminalRecordResponseDto> updateAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody CertificateOfNoCriminalRecordRequestDto dto
    ) {
        return ResponseEntity.ok(certificateService.update(employeeId, dto));
    }

    @Operation(
            summary = "Deletes a certificate of no criminal record",
            description = "Deletes a certificate of no criminal record of an employee by employee id. " +
                    "Returns no content response."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAcademicDegree(@RequestParam("id") UUID id) {
        certificateService.deleteByEmployeeId(id);
        return ResponseEntity.noContent().build();
    }
}
