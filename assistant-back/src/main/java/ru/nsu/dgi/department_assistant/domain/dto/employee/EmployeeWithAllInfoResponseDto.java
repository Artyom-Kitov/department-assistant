package ru.nsu.dgi.department_assistant.domain.dto.employee;

import java.util.List;
import java.util.UUID;

public record EmployeeWithAllInfoResponseDto(
        UUID id,
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
        Boolean hasHigherEducation,
        String snils,
        String inn,
        Boolean isArchived
) {
}
