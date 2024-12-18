package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record EmployeeResponseDto(
        String id,
        String firstName,
        String lastName,
        String middleName,
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean needsMandatoryElection,
        String snils,
        String inn,
        Boolean isArchived
) {}
