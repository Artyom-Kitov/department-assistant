package ru.nsu.dgi.department_assistant.domain.dto.process;

import java.util.UUID;

public record StepExecutedDto(
        UUID employeeId,
        UUID processId,
        int stepId
) {
}
