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
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.WorkExperienceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Work experience",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about work experience."
)
@RequestMapping("/api/v1/work-experience")
public class WorkExperienceController {
    private final WorkExperienceService workExperienceService;

    @Operation(
            summary = "Returns all work experience",
            description = "Returns all work experience according to employees that have work experience."
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
    public ResponseEntity<List<WorkExperienceResponseDto>> getAll() {
        return ResponseEntity.ok(workExperienceService.getAll());
    }

    @Operation(
            summary = "Returns a work experience of an employee",
            description = "Returns a work experience of a certain employee by employee id."
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
    public ResponseEntity<WorkExperienceResponseDto> getPassportInfoByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(workExperienceService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new work experience",
            description =
                    "Creates a new work experience for an employee by employee id. " +
                            "Operation may not be possible if a work experience is already exists for this employee."
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
    public ResponseEntity<WorkExperienceResponseDto> createWorkExperience(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody WorkExperienceRequestDto workExperienceRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workExperienceService.create(employeeId, workExperienceRequestDto));
    }

    @Operation(
            summary = "Updates a work experience",
            description =
                    "Updates a work experience for an employee by employee id. " +
                            "Does nothing if there's no work experience for chosen employee."
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
    public ResponseEntity<WorkExperienceResponseDto> updateWorkExperience(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody WorkExperienceRequestDto workExperienceRequestDto
    ) {
        return ResponseEntity.ok(workExperienceService.update(employeeId, workExperienceRequestDto));
    }

    @Operation(
            summary = "Deletes a work experience",
            description = "Deletes a work experience of an employee by employee id. " +
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
    public ResponseEntity<Void> deleteWorkExperience(
            @RequestParam("employeeId") UUID employeeId
    ) {
        workExperienceService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
