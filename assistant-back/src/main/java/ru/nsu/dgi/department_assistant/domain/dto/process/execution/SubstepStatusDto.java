package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeDto;
import java.util.UUID;

public record SubstepStatusDto(
        UUID substepId,
        boolean isCompleted,
        DocumentTypeDto documentType
) {
}
