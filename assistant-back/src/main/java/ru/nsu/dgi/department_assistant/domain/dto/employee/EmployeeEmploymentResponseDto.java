package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record EmployeeEmploymentResponseDto (
        PostResponseDto post,
        EmploymentTypeResponseDto employmentType
) {}
