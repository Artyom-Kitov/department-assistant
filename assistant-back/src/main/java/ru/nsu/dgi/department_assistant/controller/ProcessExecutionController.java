package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepStatusDto;
import ru.nsu.dgi.department_assistant.domain.service.ProcessExecutionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/execute")
@RequiredArgsConstructor
public class ProcessExecutionController {

    private final ProcessExecutionService processExecutionService;

    @PostMapping("/statuses")
    public ResponseEntity<List<StepStatusDto>> getStatuses(@RequestBody ProcessExecutionStatusRequestDto request) {
        return ResponseEntity.ok(processExecutionService.getStatuses(request));
    }

    @Operation(
            summary = "Start process execution for an employee",
            description = "Note that deadline is optional"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully started"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Process for given employee is already started"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Process or employee with given ID not found"
                    )
            }
    )
    @PostMapping("/start")
    public ResponseEntity<Void> startExecutionForEmployee(@RequestBody ProcessExecutionRequestDto request) {
        processExecutionService.startForEmployee(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/common")
    public ResponseEntity<Void> executeCommonStep(@RequestParam UUID employeeId, @RequestBody StepExecutedDto dto) {
        processExecutionService.executeCommonStep(employeeId, dto);
        return ResponseEntity.ok().build();
    }
}
