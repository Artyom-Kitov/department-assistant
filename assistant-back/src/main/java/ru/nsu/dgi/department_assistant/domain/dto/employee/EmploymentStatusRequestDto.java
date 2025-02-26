package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmploymentStatusRequestDto(
        UUID employeeId,
        Boolean isEmployedInNsu,
        String employmentInfo
) {}
