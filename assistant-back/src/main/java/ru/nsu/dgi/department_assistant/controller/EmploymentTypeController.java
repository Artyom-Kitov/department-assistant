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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentTypeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(
        name = "Employment types",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about employment types."
)
@RequestMapping("/api/v1/employment-type")
public class EmploymentTypeController {
    private final EmploymentTypeService employmentTypeService;

    @Operation(
            summary = "Returns all employment types",
            description = "Returns all existing employment types."
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
    public ResponseEntity<List<EmploymentTypeResponseDto>> getAll() {
        return ResponseEntity.ok(employmentTypeService.getAll());
    }

    @Operation(
            summary = "Returns an employment type",
            description = "Returns an employment type by its id."
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
    public ResponseEntity<EmploymentTypeResponseDto> getById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(employmentTypeService.getById(id));
    }

    @Operation(
            summary = "Creates a new employment type",
            description =
                    "Creates a new employment type by specifying its parameters."
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
    public ResponseEntity<EmploymentTypeResponseDto> create(
            @RequestBody EmploymentTypeRequestDto employmentTypeRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employmentTypeService.create(employmentTypeRequestDto));
    }

    @Operation(
            summary = "Updates an employment type",
            description =
                    "Updates an employment type by its id."
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
    public ResponseEntity<EmploymentTypeResponseDto> update(
            @RequestParam("id") Integer id,
            @RequestBody EmploymentTypeRequestDto employmentTypeRequestDto
    ) {
        return ResponseEntity.ok(employmentTypeService.update(id, employmentTypeRequestDto));
    }

    @Operation(
            summary = "Deletes an employment type",
            description = "Deletes an employment type by its id." +
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
    public ResponseEntity<Void> delete(@RequestParam("id") Integer id) {
        employmentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
