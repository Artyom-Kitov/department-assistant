package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepExecutedDto;

import java.util.UUID;

public interface ProcessExecutionService {
    void startForEmployee(ProcessExecutionRequestDto request);

    void executeCommonStep(UUID employeeId, StepExecutedDto dto);
}
