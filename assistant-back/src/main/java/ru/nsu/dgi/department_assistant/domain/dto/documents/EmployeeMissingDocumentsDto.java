package ru.nsu.dgi.department_assistant.domain.dto.documents;

import java.util.List;
import java.util.UUID;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;

public record EmployeeMissingDocumentsDto(
    UUID processId,
    String processName,
    List<DocumentTypeDto> missingDocuments
) {
    public static EmployeeMissingDocumentsDto fromEntity(Process process, List<DocumentTypeDto> missingDocuments) {
        return new EmployeeMissingDocumentsDto(
            process.getId(),
            process.getName(),
            missingDocuments
        );
    }
} 