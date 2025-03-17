package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

import java.util.List;
import java.util.UUID;

public interface ProcessGraphService {
    ProcessGraph buildGraph(UUID id, String name, List<ProcessGraphNode> nodes);

    ProcessGraph buildGraph(String name, List<ProcessGraphNode> nodes, int duration);

    int calculateDurationStartingFrom(ProcessGraph graph, int stepFrom);
}
