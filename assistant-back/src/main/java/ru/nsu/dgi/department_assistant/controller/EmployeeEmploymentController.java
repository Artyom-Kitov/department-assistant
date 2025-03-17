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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeEmploymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Employments",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about employments."
)
@RequestMapping("/api/v1/employments")
public class EmployeeEmploymentController {
    private final EmployeeEmploymentService employeeEmploymentService;

    @Operation(
            summary = "Returns all employments",
            description = "Returns all employee employments that contains an employee, employment type and post type."
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
    public ResponseEntity<List<EmployeeEmploymentResponseDto>> getAll() {
        return ResponseEntity.ok(employeeEmploymentService.getAll());
    }

    @Operation(
            summary = "Returns employments of an employee",
            description = "Returns employments of a certain employee by employee id."
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
    public ResponseEntity<List<EmployeeEmploymentResponseDto>> getEmploymentsByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(employeeEmploymentService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Returns employment by id",
            description = "Returns a single employment by its id. Id contains of employeeId, postId and " +
                    "employmentTypeId."
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
    @GetMapping("/id")
    public ResponseEntity<EmployeeEmploymentResponseDto> getEmploymentById(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        return ResponseEntity.ok(employeeEmploymentService.getById(employeeId, postId, employmentTypeId));
    }

    @Operation(
            summary = "Creates a new employment",
            description =
                    "Creates a new employment by employeeId, postId and employmentTypeId."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entity not found"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<EmployeeEmploymentResponseDto> createEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeEmploymentService.create(employeeId, postId, employmentTypeId));
    }

    @Operation(
            summary = "Updates an employment",
            description =
                    "Updates an employment by its id. Id contains of employeeId, postId and employmentTypeId."
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
    public ResponseEntity<EmployeeEmploymentResponseDto> updateEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId,
            @RequestBody EmployeeEmploymentRequestDto dto
    ) {
        return ResponseEntity.ok(employeeEmploymentService.update(employeeId, postId, employmentTypeId, dto));
    }

    @Operation(
            summary = "Deletes an employment",
            description =
                    "Deletes an employment by its id. Id contains of employeeId, postId, employmentTypeId. " +
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
    public ResponseEntity<Void> deleteEmployment(
            @RequestParam("employeeId") UUID employeeId,
            @RequestParam("postId") Integer postId,
            @RequestParam("employmentTypeId") Integer employmentTypeId
    ) {
        employeeEmploymentService.delete(employeeId, postId, employmentTypeId);
        return ResponseEntity.noContent().build();
    }
}
