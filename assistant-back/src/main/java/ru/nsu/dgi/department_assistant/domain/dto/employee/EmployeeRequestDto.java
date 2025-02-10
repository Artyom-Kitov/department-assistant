package ru.nsu.dgi.department_assistant.domain.dto.employee;

public record EmployeeRequestDto(
        String firstName,
        String lastName,
        String middleName,
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean hasHigherEducation,
        Boolean needsMandatoryElection,
        String snils,
        String inn,
        Boolean isArchived
) {}
