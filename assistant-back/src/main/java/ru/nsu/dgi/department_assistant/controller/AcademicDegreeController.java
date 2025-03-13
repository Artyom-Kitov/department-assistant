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
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.AcademicDegreeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/academic-degree")
@Tag(
        name = "Academic degrees",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "academic degree information."
)
public class AcademicDegreeController {
    private final AcademicDegreeService academicDegreeService;

    @Operation(
            summary = "Returns all academic degrees",
            description = "Returns the list of academic degrees of all employees."
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
    public ResponseEntity<List<AcademicDegreeResponseDto>> getAllAcademicDegrees() {
        return ResponseEntity.ok(academicDegreeService.getAll());
    }

    @Operation(
            summary = "Returns academic degree of an employee",
            description = "Returns an academic degree of a certain employee by id of employee."
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
    public ResponseEntity<AcademicDegreeResponseDto> getAcademicDegreeByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(academicDegreeService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new academic degree",
            description =
                    "Creates a new academic degree for an employee by employee id. Operation may not be possible " +
                    "if an academic degree is already exists for this employee."
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
    public ResponseEntity<AcademicDegreeResponseDto> createAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(academicDegreeService.create(employeeId, academicDegree));
    }

    @Operation(
            summary = "Updates an academic degree",
            description =
                    "Updates an academic degree for an employee by employee id. Does nothing if there's no " +
                    "academic degree for chosen employee."
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
    public ResponseEntity<AcademicDegreeResponseDto> updateAcademicDegree(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody AcademicDegreeRequestDto academicDegree
    ) {
        return ResponseEntity.ok(academicDegreeService.update(employeeId, academicDegree));
    }

    @Operation(
            summary = "Deletes an academic degree",
            description = "Deletes an academic degree of an employee by employee id. Returns no content response."
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
    public ResponseEntity<Void> deleteAcademicDegree(@RequestParam("employeeId") UUID employeeId) {
        academicDegreeService.deleteByEmployeeId(employeeId);
        return ResponseEntity.noContent().build();
    }
}
