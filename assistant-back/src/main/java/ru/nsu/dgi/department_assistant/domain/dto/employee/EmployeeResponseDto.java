package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.UUID;

public record EmployeeResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean needsMandatoryElection,
        Boolean hasHigherEducation,
        String snils,
        String inn,
        Boolean isArchived
) {}
