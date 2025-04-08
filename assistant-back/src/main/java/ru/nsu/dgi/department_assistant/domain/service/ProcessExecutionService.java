package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ConditionalExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.EmployeeProcessExecutionDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessCancellationDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateShortDto;

import java.util.List;
import java.util.UUID;

public interface ProcessExecutionService {
    List<ProcessExecutionStatusDto> getProcessStatuses();

    void startForEmployee(ProcessExecutionRequestDto request);

    void cancel(ProcessCancellationDto request);

    void executeCommonStep(StepExecutedDto dto);

    void executeSubstep(SubstepExecutedDto dto);

    void executeConditional(ConditionalExecutedDto dto);

    EmployeeProcessExecutionDto getStatuses(UUID employeeId, UUID processId);

    List<ProcessTemplateShortDto> getByEmployee(UUID employeeId);
}
