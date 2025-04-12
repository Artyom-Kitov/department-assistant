package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ConditionalExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.EmployeeProcessExecutionDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessCancellationDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepCancellationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepCancellationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateShortDto;
import ru.nsu.dgi.department_assistant.domain.service.ProcessExecutionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/execution")
@RequiredArgsConstructor
@Tag(
        name = "Execution",
        description = "Provides process execution methods."
)
public class ProcessExecutionController {

    private final ProcessExecutionService processExecutionService;

    @Operation(
            summary = "Get executed processes",
            description = "Get all currently executed processes. Current step status for each process is also returned."
    )
    @GetMapping("/all")
    public ResponseEntity<List<ProcessExecutionStatusDto>> getProcessStatuses() {
        return ResponseEntity.ok(processExecutionService.getProcessStatuses());
    }

    @Operation(
            summary = "Get all statuses of the given employee in the given process",
            description = "Note that if subtasks list of a substep status is null, it's not a step with subtasks. " +
                    "isSuccessful flag is needed for conditional steps to see which branch has been chosen."
    )
    @GetMapping("/employee")
    public ResponseEntity<EmployeeProcessExecutionDto> getStatuses(@RequestParam UUID employeeId,
                                                                   @RequestParam UUID processId) {
        return ResponseEntity.ok(processExecutionService.getStatuses(employeeId, processId));
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

    @Operation(summary = "Cancel process execution")
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelExecution(@RequestBody ProcessCancellationDto request) {
        processExecutionService.cancel(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/common")
    public ResponseEntity<Void> executeCommonStep(@RequestBody StepExecutedDto dto) {
        processExecutionService.executeCommonStep(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/substep")
    public ResponseEntity<Void> executeSubstep(@RequestBody SubstepExecutedDto dto) {
        processExecutionService.executeSubstep(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conditional")
    public ResponseEntity<Void> executeConditional(@RequestBody ConditionalExecutedDto dto) {
        processExecutionService.executeConditional(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProcessTemplateShortDto>> getByEmployee(@RequestParam UUID employeeId) {
        return ResponseEntity.ok(processExecutionService.getByEmployee(employeeId));
    }

    @PostMapping("/step/cancel")
    public ResponseEntity<Void> cancelStep(@RequestBody StepCancellationRequestDto request) {
        processExecutionService.cancelStep(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/substep/cancel")
    public ResponseEntity<Void> cancelSubstep(@RequestBody SubstepCancellationRequestDto request) {
        processExecutionService.cancelSubstep(request);
        return ResponseEntity.ok().build();
    }
}
