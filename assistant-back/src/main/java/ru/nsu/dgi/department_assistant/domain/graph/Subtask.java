package ru.nsu.dgi.department_assistant.domain.graph;

import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import java.util.UUID;

public record Subtask(UUID id, String description, int duration) implements SubtaskLike {
    @Override
    public DocumentType documentType() {
        return null;
    }
}
