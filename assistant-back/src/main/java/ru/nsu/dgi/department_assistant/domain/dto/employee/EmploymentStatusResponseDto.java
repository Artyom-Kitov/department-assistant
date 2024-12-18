package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record EmploymentStatusResponseDto(
        Integer id,
        Boolean isEmployedInNsu,
        String employmentInfo
) {}
