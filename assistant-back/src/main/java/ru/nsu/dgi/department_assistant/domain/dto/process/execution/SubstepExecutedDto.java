package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.UUID;

public record SubstepExecutedDto(
        UUID employeeId,
        UUID startProcessId,
        UUID substepId
) {
}
