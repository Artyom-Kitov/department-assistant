package ru.nsu.dgi.department_assistant.domain.graph;

import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import java.util.UUID;

public interface SubtaskLike {
    UUID id();
    String description();
    int duration();
    DocumentType documentType();
} 