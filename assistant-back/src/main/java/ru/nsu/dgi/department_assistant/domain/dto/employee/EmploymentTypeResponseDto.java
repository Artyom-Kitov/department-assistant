package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmploymentTypeResponseDto(
        Integer id,
        UUID employeeId,
        String name
) {}
