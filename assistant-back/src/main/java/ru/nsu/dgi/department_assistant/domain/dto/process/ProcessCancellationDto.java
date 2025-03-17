package ru.nsu.dgi.department_assistant.domain.dto.process;

import java.util.UUID;

public record ProcessCancellationDto(
        UUID employeeId,
        UUID processId
) {
}
