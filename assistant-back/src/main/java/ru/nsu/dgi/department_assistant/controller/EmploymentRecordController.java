
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
@Tag(
        name = "Employment records",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about employment records."
)
@RequestMapping("/api/v1/employment-records")
public class EmploymentRecordController {
    private final EmploymentRecordService employmentRecordService;

    @Operation(
            summary = "Returns all employment records",
            description = "Returns all employment records according to employees that have employment record."
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
    public ResponseEntity<List<EmploymentRecordResponseDto>> getAllEmploymentRecords() {
        return ResponseEntity.ok(employmentRecordService.getAll());
    }

    @Operation(
            summary = "Returns an employment record of an employee",
            description = "Returns an employment record of a certain employee by employee id."
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
    public ResponseEntity<EmploymentRecordResponseDto> getByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employmentRecordService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new employment record",
            description =
                    "Creates a new employment record for an employee by employee id. " +
                            "Operation may not be possible if an employment record is already exists for this employee."
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
    public ResponseEntity<EmploymentRecordResponseDto> createEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentRecordRequestDto employmentRecordRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentRecordService.create(employeeId, employmentRecordRequestDto));
    }

    @Operation(
            summary = "Updates an employment record",
            description =
                    "Updates an employment record for an employee by employee id. " +
                            "Does nothing if there's no employment record for chosen employee."
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
    public ResponseEntity<EmploymentRecordResponseDto> updateEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentRecordRequestDto employmentRecordRequestDto
    ) {
        return ResponseEntity.ok(employmentRecordService.update(employeeId, employmentRecordRequestDto));
    }

    @Operation(
            summary = "Deletes an employment record",
            description = "Deletes an employment record of an employee by employee id. " +
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
    public ResponseEntity<Void> deleteEmploymentRecord(
            @RequestParam("employeeId") UUID employeeId
    ) {
        employmentRecordService.deleteByEmployeeId(employeeId);
        return ResponseEntity.noContent().build();
    }
}
