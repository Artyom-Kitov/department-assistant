package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.UUID;

public interface ProcessSavingService {
    UUID saveTemplate(String name, int totalDuration, ProcessGraphNode root);

    ProcessGraph loadTemplate(UUID id);
}
