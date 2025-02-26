package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record EmploymentStatusRequestDto(
        Boolean isEmployedInNsu,
        String employmentInfo
) {
}
