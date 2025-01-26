package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;

import java.util.UUID;

public interface ProcessSavingService {
    void saveTemplate(ProcessGraph graph);

    ProcessGraph loadTemplate(UUID id);
}
