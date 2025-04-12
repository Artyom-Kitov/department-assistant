package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.UUID;

public record StepCancellationRequestDto(
        UUID employeeId,
        UUID startProcessId,
        UUID processId,
        int stepId
) {
}
