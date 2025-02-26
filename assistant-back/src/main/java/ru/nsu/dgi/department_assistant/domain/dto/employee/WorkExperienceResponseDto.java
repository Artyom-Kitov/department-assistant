package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record WorkExperienceResponseDto(
        Integer id,
        UUID employeeId,
        Integer days
) {
}
