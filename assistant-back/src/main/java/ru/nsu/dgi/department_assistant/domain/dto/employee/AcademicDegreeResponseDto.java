package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record AcademicDegreeResponseDto(
        UUID employeeId,
        Integer id,
        String name
) {
}
