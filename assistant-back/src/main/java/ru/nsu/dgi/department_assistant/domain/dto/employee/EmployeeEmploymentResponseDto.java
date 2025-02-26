package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmployeeEmploymentResponseDto(
        UUID employeeId,
        PostResponseDto post,
        EmploymentTypeResponseDto employmentType
) {
}
