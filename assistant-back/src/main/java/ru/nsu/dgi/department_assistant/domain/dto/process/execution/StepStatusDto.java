package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record StepStatusDto(
        UUID employeeId,
        UUID processId,
        int stepId,
        UUID startProcessId,
        LocalDate deadline,
        @Nullable LocalDate completedAt,
        @Nullable Boolean isSuccessful,
        @Nullable List<SubstepStatusDto> substepStatuses
) {
}
