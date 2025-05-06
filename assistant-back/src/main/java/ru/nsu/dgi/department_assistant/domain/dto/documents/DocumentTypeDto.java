package ru.nsu.dgi.department_assistant.domain.dto.documents;

import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;

public record DocumentTypeDto(
    Long id,
    String name
) {
    public static DocumentTypeDto fromEntity(DocumentType entity) {
        return new DocumentTypeDto(
            entity.getId(),
            entity.getName()
        );
    }
} 