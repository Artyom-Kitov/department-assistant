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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentStatusService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Employment statuses",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about employment statuses."
)
@RequestMapping("/api/v1/employment-status")
public class EmploymentStatusController {
    private final EmploymentStatusService employmentStatusService;

    @Operation(
            summary = "Returns all employment statuses",
            description = "Returns all employment statuses according to employees that have employment record."
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
    public ResponseEntity<List<EmploymentStatusResponseDto>> getEmploymentStatus() {
        return ResponseEntity.ok(employmentStatusService.getAll());
    }

    @Operation(
            summary = "Returns an employment status of an employee",
            description = "Returns an employment status of a certain employee by employee id."
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
    public ResponseEntity<EmploymentStatusResponseDto> getByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employmentStatusService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new employment status",
            description =
                    "Creates a new employment status for an employee by employee id. " +
                            "Operation may not be possible if an employment status is already exists for this employee."
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
    public ResponseEntity<EmploymentStatusResponseDto> createEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentStatusRequestDto employmentStatusRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentStatusService.create(employeeId, employmentStatusRequestDto));
    }

    @Operation(
            summary = "Updates an employment status",
            description =
                    "Updates an employment status for an employee by employee id. " +
                            "Does nothing if there's no employment status for chosen employee."
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
    public ResponseEntity<EmploymentStatusResponseDto> updateEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody EmploymentStatusRequestDto employmentStatusRequestDto
    ) {
        return ResponseEntity.ok(employmentStatusService.update(employeeId, employmentStatusRequestDto));
    }

    @Operation(
            summary = "Deletes an employment status",
            description = "Deletes an employment status of an employee by employee id. " +
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
    public ResponseEntity<EmploymentStatusResponseDto> deleteEmploymentStatus(
            @RequestParam("employeeId") UUID employeeId
    ) {
        employmentStatusService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
