package ru.nsu.dgi.department_assistant.domain.graph;

import java.util.UUID;

public record Subtask(UUID id, String description, int duration) {
}
