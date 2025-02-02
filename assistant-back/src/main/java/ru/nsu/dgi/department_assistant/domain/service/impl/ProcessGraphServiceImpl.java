package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessGraphServiceImpl implements ProcessGraphService {

    private final ProcessRepository processRepository;

    @Override
    public ProcessGraph buildGraph(UUID id, String name, List<ProcessGraphNode> nodes) {
        Map<Integer, ProcessGraphNode> nodesMap = nodes.stream()
                .collect(Collectors.toMap(ProcessGraphNode::getId, node -> node));
        ProcessGraphNode root = findHeadNode(nodesMap);
        validateNoLoops(root.getId(), nodesMap, new HashSet<>(), new HashSet<>());
        int duration = calculateDuration(root.getId(), nodesMap);
        return ProcessGraph.builder()
                .id(id)
                .name(name)
                .duration(duration)
                .root(root.getId())
                .nodes(nodesMap)
                .build();
    }

    @Override
    public ProcessGraph buildGraph(String name, List<ProcessGraphNode> nodes, int duration) {
        Map<Integer, ProcessGraphNode> nodesMap = nodes.stream()
                .collect(Collectors.toMap(ProcessGraphNode::getId, node -> node));
        ProcessGraphNode root = findHeadNode(nodesMap);
        return ProcessGraph.builder()
                .id(UUID.randomUUID())
                .name(name)
                .duration(duration)
                .root(root.getId())
                .nodes(nodesMap)
                .build();
    }

    private ProcessGraphNode findHeadNode(Map<Integer, ProcessGraphNode> nodes) {
        Set<Integer> notVisited = nodes.values().stream()
                .map(ProcessGraphNode::getId)
                .collect(Collectors.toSet());
        nodes.forEach((key, node) -> node.next().forEach(notVisited::remove));
        if (notVisited.size() != 1) {
            throw new InvalidProcessTemplateException("multiple roots");
        }
        return nodes.get(notVisited.stream().findAny().orElseThrow());
    }

    private void validateNoLoops(int nodeId, Map<Integer, ProcessGraphNode> nodes,
                                 Set<Integer> visited, Set<Integer> inStack) {
        if (inStack.contains(nodeId)) {
            throw new InvalidProcessTemplateException("process contains loops");
        }
        if (visited.contains(nodeId)) {
            return;
        }

        visited.add(nodeId);
        inStack.add(nodeId);
        for (int next : nodes.get(nodeId).next()) {
            validateNoLoops(next, nodes, visited, inStack);
        }
        inStack.remove(nodeId);
    }

    private int calculateDuration(int start, Map<Integer, ProcessGraphNode> nodes) {
        ProcessGraphNode node = nodes.get(start);
        return switch (node.getData()) {
            case CommonStepData data -> node.getDuration() + calculateDuration(data.getNext(), nodes);
            case ConditionalStepData data -> node.getDuration() + Math.max(
                    calculateDuration(data.getIfTrue(), nodes),
                    calculateDuration(data.getIfFalse(), nodes)
            );
            case SubtasksStepData data -> node.getDuration() + data.getSubtasks().stream()
                    .mapToInt(Subtask::duration)
                    .max()
                    .orElseThrow(() -> new InvalidProcessTemplateException("no subtasks found"))
                    + calculateDuration(data.getNext(), nodes);
            case FinalData ignored -> 0;
            case ProcessTransitionStepData data -> processRepository.findById(data.getNextProcess())
                    .orElseThrow(() -> new EntityNotFoundException(data.getNextProcess().toString()))
                    .getTotalDuration();
        };
    }
}
