package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.List;

public record EmployeeWithAllInfoResponseDto (
        String id,
        String firstName,
        String lastName,
        String middleName,
        ContactsResponseDto contacts,
        AcademicDegreeResponseDto academicDegree,
        EmploymentStatusResponseDto employmentStatus,
        EmploymentRecordResponseDto employmentRecord,
        PassportInfoResponseDto passportInfo,
        WorkExperienceResponseDto workExperience,
        CertificateOfNoCriminalRecordResponseDto certificateOfNoCriminalRecord,
        List<EmployeeEmploymentResponseDto> employments,
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean needsMandatoryElection,
        String snils,
        String inn,
        Boolean isArchived
) {}
