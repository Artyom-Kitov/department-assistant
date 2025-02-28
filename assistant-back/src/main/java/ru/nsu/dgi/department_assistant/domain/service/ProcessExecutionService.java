package ru.nsu.dgi.department_assistant.domain.service;

import org.springframework.http.ResponseEntity;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepStatusDto;

import java.util.List;
import java.util.UUID;

public interface ProcessExecutionService {
    void startForEmployee(ProcessExecutionRequestDto request);

    void executeCommonStep(UUID employeeId, StepExecutedDto dto);

    List<StepStatusDto> getStatuses(ProcessExecutionStatusRequestDto request);
}
