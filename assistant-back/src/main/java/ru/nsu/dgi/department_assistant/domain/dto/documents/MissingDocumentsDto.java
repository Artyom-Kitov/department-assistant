package ru.nsu.dgi.department_assistant.domain.dto.documents;

import java.util.List;
import java.util.UUID;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

public record MissingDocumentsDto(
    UUID employeeId,
    String employeeName,
    List<DocumentTypeDto> missingDocuments
) {
    public static MissingDocumentsDto fromEntity(Employee employee, List<DocumentTypeDto> missingDocuments) {
        return new MissingDocumentsDto(
            employee.getId(),
            formatEmployeeName(employee),
            missingDocuments
        );
    }

    private static String formatEmployeeName(Employee employee) {
        return String.format("%s %s %s",
            employee.getLastName(),
            employee.getFirstName(),
            employee.getMiddleName() != null ? employee.getMiddleName() : ""
        ).trim();
    }
} 