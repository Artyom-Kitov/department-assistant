package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmployeeEmploymentRequestDto(
        UUID employeeId,
        PostRequestDto post,
        EmploymentTypeRequestDto employmentType
) {}
