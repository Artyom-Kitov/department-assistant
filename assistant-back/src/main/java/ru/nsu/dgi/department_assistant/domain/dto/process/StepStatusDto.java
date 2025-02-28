package ru.nsu.dgi.department_assistant.domain.dto.process;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.UUID;

public record StepStatusDto(
        int stepId,
        UUID startProcessId,
        LocalDate deadline,
        @Nullable LocalDate completedAt,
        @Nullable Boolean isSuccessful
) {
}
