package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

public interface ProcessGraphService {
    int calculateDuration(ProcessGraphNode node);
}
