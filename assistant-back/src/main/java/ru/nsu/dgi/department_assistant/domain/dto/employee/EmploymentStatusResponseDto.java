package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmploymentStatusResponseDto(
        Integer id,
        UUID employeeId,
        Boolean isEmployedInNsu,
        String employmentInfo
) {}
