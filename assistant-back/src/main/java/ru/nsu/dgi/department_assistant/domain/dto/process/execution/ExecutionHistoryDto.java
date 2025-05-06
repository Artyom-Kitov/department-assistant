package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.time.LocalDate;
import java.util.UUID;

public record ExecutionHistoryDto(
        UUID id,
        UUID employeeId,
        UUID processId,
        LocalDate startedAt,
        LocalDate completedAt,
        String result,
        boolean isSuccessful
) {
}
