package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.UUID;

public interface ProcessSavingService {
    UUID saveTemplateToDb(String name, int totalDuration, ProcessGraphNode root);
}
