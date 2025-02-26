package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.List;

public record EmployeeWithAllInfoRequestDto(
        String firstName,
        String lastName,
        String middleName,
        ContactsRequestDto contacts,
        AcademicDegreeRequestDto academicDegree,
        EmploymentStatusRequestDto employmentStatus,
        EmploymentRecordRequestDto employmentRecord,
        PassportInfoRequestDto passportInfo,
        WorkExperienceRequestDto workExperience,
        CertificateOfNoCriminalRecordRequestDto certificateOfNoCriminalRecord,
        List<EmployeeEmploymentRequestDto> employments,
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean needsMandatoryElection,
        String snils,
        String inn,
        Boolean isArchived
) {}
