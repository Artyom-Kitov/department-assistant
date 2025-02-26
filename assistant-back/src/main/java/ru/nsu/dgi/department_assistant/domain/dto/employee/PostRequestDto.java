package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record PostRequestDto(
        UUID employeeId,
        String name
) {}
