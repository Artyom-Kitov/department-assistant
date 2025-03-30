package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.List;

public record EmployeeProcessExecutionDto(
        List<StepStatusDto> completed,
        StepStatusDto current,
        List<StepStatusDto> toComplete
) {
}
