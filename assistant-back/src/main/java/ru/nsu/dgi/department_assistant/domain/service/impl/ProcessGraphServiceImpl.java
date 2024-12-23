package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessStepDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.dto.process.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.exception.ProcessLoopException;
import ru.nsu.dgi.department_assistant.domain.graph.CommonStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.ConditionalStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.FinalNode;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessTransitionNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;
import ru.nsu.dgi.department_assistant.domain.graph.SubtasksStepNode;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessGraphServiceImpl implements ProcessGraphService {

    @Override
    public ProcessGraphNode buildFromRequest(ProcessTemplateCreationRequestDto request) {
        Map<UUID, ProcessStepDto> steps = new HashMap<>();
        request.steps().forEach(step -> steps.put(step.id(), step));

        Map<UUID, ProcessGraphNode> nodes = buildNodes(steps);
        ProcessGraphNode head = nodes.get(findHeadId(steps));
        validateNoLoops(head);
        return head;
    }

    private UUID findHeadId(Map<UUID, ProcessStepDto> steps) {
        Set<UUID> noIncoming = new HashSet<>(steps.keySet());
        for (var entry : steps.entrySet()) {
            entry.getValue().data().next().forEach(noIncoming::remove);
        }
        if (noIncoming.size() != 1) {
            throw new InvalidProcessTemplateException("process should contain only one root step");
        }
        return noIncoming.stream()
                .findAny()
                .orElseThrow();
    }

    private Map<UUID, ProcessGraphNode> buildNodes(Map<UUID, ProcessStepDto> steps) {
        Map<UUID, ProcessGraphNode> mappedSteps = new HashMap<>();
        steps.forEach((id, step) -> mappedSteps.put(id, step.asGraphNode()));
        resolveNextSteps(steps, mappedSteps);
        return mappedSteps;
    }

    private void resolveNextSteps(Map<UUID, ProcessStepDto> steps, Map<UUID, ProcessGraphNode> nodes) {
        nodes.forEach((id, node) -> {
            switch (node) {
                case CommonStepNode n -> {
                    UUID nextId = ((CommonStepData) getNextStepData(steps, id)).getNext();
                    n.setNext(nodes.get(nextId));
                }
                case ConditionalStepNode n -> {
                    UUID ifTrueId = ((ConditionalStepData) getNextStepData(steps, id)).getIfTrue();
                    UUID ifFalseId = ((ConditionalStepData) getNextStepData(steps, id)).getIfFalse();
                    n.setIfTrue(nodes.get(ifTrueId));
                    n.setIfFalse(nodes.get(ifFalseId));
                }
                case FinalNode ignored -> {}
                case ProcessTransitionNode ignored -> {}
                case SubtasksStepNode n -> {
                    UUID nextId = ((SubtasksStepData) getNextStepData(steps, id)).getNext();
                    n.setNext(nodes.get(nextId));
                }
            }
        });
    }

    private StepData getNextStepData(Map<UUID, ProcessStepDto> steps, UUID id) {
        ProcessStepDto step = steps.get(id);
        if (step == null) {
            throw new InvalidProcessTemplateException("no step with id " + id);
        }
        return step.data();
    }

    private void validateNoLoops(ProcessGraphNode node) {
        Set<UUID> visited = new HashSet<>();
        Queue<ProcessGraphNode> nodes = new LinkedList<>();
        nodes.add(node);
        while (!nodes.isEmpty()) {
            ProcessGraphNode current = nodes.poll();
            visited.add(current.getId());
            switch (current) {
                case CommonStepNode n -> {
                    if (visited.contains(n.getNext().getId())) {
                        throw new ProcessLoopException();
                    }
                    nodes.add(n.getNext());
                }
                case SubtasksStepNode n -> {
                    if (visited.contains(n.getNext().getId())) {
                        throw new ProcessLoopException();
                    }
                    nodes.add(n.getNext());
                }
                case ConditionalStepNode n -> {
                    if (visited.contains(n.getIfTrue().getId()) || visited.contains(n.getIfFalse().getId())) {
                        throw new ProcessLoopException();
                    }
                    nodes.add(n.getIfTrue());
                    nodes.add(n.getIfFalse());
                }
                case FinalNode ignored -> {}
                case ProcessTransitionNode ignored -> {}
            }
        }
    }

    @Override
    public int calculateDuration(ProcessGraphNode node) {
        return switch (node) {
            case CommonStepNode n -> node.getDuration() + calculateDuration(n.getNext());
            case ConditionalStepNode n -> n.getDuration() + Math.max(
                    calculateDuration(n.getIfTrue()),
                    calculateDuration(n.getIfFalse())
            );
            case SubtasksStepNode n -> n.getSubtasks().stream()
                    .mapToInt(Subtask::duration)
                    .max()
                    .orElseThrow() + calculateDuration(n.getNext());
            case FinalNode ignored -> 0;
            case ProcessTransitionNode ignored -> 0;
        };
    }

    @Transactional
    @Override
    public void saveToDatabase(ProcessGraphNode node) {

    }
}
