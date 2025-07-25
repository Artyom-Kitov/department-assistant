package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.UUID;

public record StepExecutedDto(
        UUID employeeId,
        UUID startProcessId,
        UUID processId,
        int stepId
) {
}
