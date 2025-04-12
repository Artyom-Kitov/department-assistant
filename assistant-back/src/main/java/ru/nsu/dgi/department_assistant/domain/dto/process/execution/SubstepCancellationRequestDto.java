package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.UUID;

public record SubstepCancellationRequestDto(
        UUID employeeId,
        UUID startProcessId,
        UUID substepId
) {
}
