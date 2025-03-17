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
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.PassportInfoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Passport info",
        description = "Provides basic operations for creating, updating, deleting and retrieving " +
                "information about passport info."
)
@RequestMapping("/api/v1/passport-info")
public class PassportInfoController {
    private final PassportInfoService passportInfoService;

    @Operation(
            summary = "Returns all passport info",
            description = "Returns all passport info according to employees that have passport info."
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
    public ResponseEntity<List<PassportInfoResponseDto>> getAll() {
        return ResponseEntity.ok(passportInfoService.getAll());
    }

    @Operation(
            summary = "Returns a passport info of an employee",
            description = "Returns a passport info of a certain employee by employee id."
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
    public ResponseEntity<PassportInfoResponseDto> getPassportInfoByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(passportInfoService.getByEmployeeId(employeeId));
    }

    @Operation(
            summary = "Creates a new passport info",
            description =
                    "Creates a new passport info for an employee by employee id. " +
                            "Operation may not be possible if a passport info is already exists for this employee."
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
    public ResponseEntity<PassportInfoResponseDto> createPassportInfo(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody PassportInfoRequestDto passportInfoRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(passportInfoService.create(employeeId, passportInfoRequestDto));
    }

    @Operation(
            summary = "Updates a passport info",
            description =
                    "Updates a passport info for an employee by employee id. " +
                            "Does nothing if there's no passport info for chosen employee."
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
    public ResponseEntity<PassportInfoResponseDto> updatePassportInfo(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody PassportInfoRequestDto passportInfoRequestDto
    ) {
        return ResponseEntity.ok(passportInfoService.update(employeeId, passportInfoRequestDto));
    }

    @Operation(
            summary = "Deletes a passport info",
            description = "Deletes a passport info of an employee by employee id. " +
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
    public ResponseEntity<Void> deletePassportInfo(
            @RequestParam("employeeId") UUID employeeId
    ) {
        passportInfoService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
