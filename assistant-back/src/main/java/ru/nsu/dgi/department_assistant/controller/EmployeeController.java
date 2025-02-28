package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @Operation(
            summary = "Returns all employees",
            description = "Returns all employees without additional information such as contacts, passport info etc."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(
            summary = "Returns an employee by id",
            description = "Returns an employee by id without additional information" +
                    " such as contacts, passport info etc."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Couldn't find an employee with given id"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @Operation(
            summary = "Returns all employees with info",
            description = "Returns all employees with all additional information such as contacts, passport info etc."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    )
            }
    )
    @GetMapping("/info")
    public ResponseEntity<List<EmployeeWithAllInfoResponseDto>> getAllEmployeesWithInfo() {
        return ResponseEntity.ok(employeeService.getAllEmployeeWithAllInfos());
    }

    @Operation(
            summary = "Returns an employee with info by id",
            description = "Returns an employee by id with all additional information" +
                    " such as contacts, passport info etc."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Couldn't find an employee with given id"
                    )
            }
    )
    @GetMapping("/info/{id}")
    public ResponseEntity<EmployeeWithAllInfoResponseDto> getEmployeeWithInfoById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeWithAllInfos(id));
    }

    @Operation(
            summary = "Creates a new employee",
            description = "Creates a new employee with parameters given via request body"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employeeRequestDto));
    }

    @Operation(
            summary = "Updates an employee by id",
            description = "Updates an employee by id with parameters given via request body"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Couldn't find an employee with given id"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable("id") UUID id,
            @RequestBody EmployeeRequestDto employeeRequestDto
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeRequestDto));
    }

    @Operation(
            summary = "Deletes an employee by id",
            description = "Deletes an employee by id and returns no content response"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Couldn't find an employee with given id"
                    )
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@RequestParam("id") UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}
