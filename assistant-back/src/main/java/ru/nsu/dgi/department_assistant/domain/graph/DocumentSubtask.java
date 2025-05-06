package ru.nsu.dgi.department_assistant.domain.graph;

import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import java.util.UUID;

public record DocumentSubtask(
    Subtask subtask,
    DocumentType documentType
) implements SubtaskLike {
    public DocumentSubtask {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        if (documentType == null) {
            throw new IllegalArgumentException("Document type cannot be null");
        }
    }

    @Override
    public UUID id() {
        return subtask.id();
    }

    @Override
    public String description() {
        return subtask.description();
    }

    @Override
    public int duration() {
        return subtask.duration();
    }
}
