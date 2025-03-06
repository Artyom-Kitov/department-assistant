package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StartStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
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
        ProcessGraphNode start = findStartNode(nodesMap);
        validateNoLoops(start.getId(), nodesMap, new HashSet<>(), new HashSet<>());
        int duration = new ProcessDurationCalculator(processRepository).calculateDuration(start.getId(), nodesMap);
        return ProcessGraph.builder()
                .id(id)
                .name(name)
                .duration(duration)
                .start(start.getId())
                .nodes(nodesMap)
                .build();
    }

    @Override
    public ProcessGraph buildGraph(String name, List<ProcessGraphNode> nodes, int duration) {
        Map<Integer, ProcessGraphNode> nodesMap = nodes.stream()
                .collect(Collectors.toMap(ProcessGraphNode::getId, node -> node));
        ProcessGraphNode start = findStartNode(nodesMap);
        return ProcessGraph.builder()
                .id(UUID.randomUUID())
                .name(name)
                .duration(duration)
                .start(start.getId())
                .nodes(nodesMap)
                .build();
    }

    private ProcessGraphNode findStartNode(Map<Integer, ProcessGraphNode> nodes) {
        List<ProcessGraphNode> candidates = nodes.values().stream()
                .filter(node -> node.getType() == StepType.START.getValue())
                .toList();
        if (candidates.size() != 1) {
            throw new InvalidProcessTemplateException("invalid amount of start steps");
        }
        return candidates.getFirst();
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

    @RequiredArgsConstructor
    private static class ProcessDurationCalculator {
        private final Set<UUID> visitedProcesses = new HashSet<>();
        private final ProcessRepository processRepository;

        public int calculateDuration(int start, Map<Integer, ProcessGraphNode> nodes) {
            ProcessGraphNode node = nodes.get(start);
            return switch (node.getData()) {
                case StartStepData data -> calculateDuration(data.getNext(), nodes);
                case CommonStepData data -> node.getDuration() + calculateDuration(data.getNext(), nodes);
                case ConditionalStepData data -> node.getDuration() + Math.max(
                        calculateDuration(data.ifTrue(), nodes),
                        calculateDuration(data.ifFalse(), nodes)
                );
                case SubtasksStepData data -> node.getDuration() + calculateDuration(data.getNext(), nodes);
                case FinalData ignored -> 0;
                case ProcessTransitionStepData data -> {
                    UUID processId = data.nextProcess();
                    if (visitedProcesses.contains(processId)) {
                        throw new InvalidProcessTemplateException("process transitions form a loop");
                    }
                    visitedProcesses.add(processId);
                    yield processRepository.findById(data.nextProcess())
                            .orElseThrow(() -> new EntityNotFoundException(data.nextProcess().toString()))
                            .getTotalDuration();
                }
            };
        }
    }


}
