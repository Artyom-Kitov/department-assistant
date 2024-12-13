package ru.nsu.dgi.department_assistant.domain.dto;

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
        Boolean agreement,
        Boolean hasCompletedAdvancedCourses,
        Boolean needsMandatoryElection,
        String snils,
        String inn,
        Boolean isArchived
) {}
