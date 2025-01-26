package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.entity.process.CommonTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.ConditionalTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.FinalType;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.ProcessTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.Substep;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.Subtask;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.repository.process.CommonTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ConditionalTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.FinalTypeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessSavingServiceImpl implements ProcessSavingService {

    private final ProcessRepository processRepository;
    private final StepRepository stepRepository;
    private final SubstepRepository substepRepository;
    private final CommonTransitionRepository commonTransitionRepository;
    private final ConditionalTransitionRepository conditionalTransitionRepository;
    private final FinalTypeRepository finalTypeRepository;
    private final ProcessTransitionRepository processTransitionRepository;

    private final ProcessGraphService processGraphService;

    @Transactional
    @Override
    public void saveTemplate(ProcessGraph graph) {
        Process process = new Process(graph.id(), graph.name(), graph.duration());
        processRepository.save(process);
        saveStep(graph.root(), graph);
    }

    private void saveStep(int nodeId, ProcessGraph graph) {
        if (stepRepository.existsById(new StepId(nodeId, graph.id()))) {
            return;
        }

        ProcessGraphNode node = graph.getNode(nodeId);
        Step step = new Step(node.getId(), graph.id(), node.getDuration(), node.getMetaInfo(),
                node.getType(), node.getDescription());
        step = stepRepository.save(step);
        switch (node.getData()) {
            case CommonStepData data -> saveCommonStep(graph, step, data);
            case ConditionalStepData data -> saveConditionalStep(graph, step, data);
            case FinalData data -> saveFinal(step, data);
            case ProcessTransitionStepData data -> saveProcessTransition(step, data);
            case SubtasksStepData data -> saveSubtasksStep(graph, step, data);
        }
    }

    private Step findStepById(StepId id) {
        return stepRepository.findById(id)
                .orElseThrow(() -> new InvalidProcessTemplateException("no step with id " + id));
    }

    private void saveCommonStep(ProcessGraph graph, Step step, CommonStepData data) {
        saveStep(data.getNext(), graph);

        StepId id = new StepId(data.getNext(), graph.id());
        Step nextStep = findStepById(id);
        CommonTransition transition = new CommonTransition(graph.id(), step.getId(), nextStep.getId());
        commonTransitionRepository.save(transition);
    }

    private void saveConditionalStep(ProcessGraph graph, Step step, ConditionalStepData data) {
        saveStep(data.getIfTrue(), graph);
        saveStep(data.getIfFalse(), graph);

        StepId ifTrueId = new StepId(data.getIfTrue(), graph.id());
        StepId ifFalseId = new StepId(data.getIfFalse(), graph.id());
        ConditionalTransition transition = new ConditionalTransition(graph.id(), step.getId(),
                ifTrueId.getId(), ifFalseId.getId());
        conditionalTransitionRepository.save(transition);
    }

    private void saveFinal(Step step, FinalData data) {
        FinalType finalType = new FinalType(step.getProcessId(), step.getId(), data.isSuccessful());
        finalTypeRepository.save(finalType);
    }

    private void saveProcessTransition(Step step, ProcessTransitionStepData data) {
        Process next = processRepository.findById(data.getNextProcess())
                .orElseThrow(() -> new InvalidProcessTemplateException("no process with id " + data.getNextProcess()));
        ProcessTransition transition = new ProcessTransition(step.getProcessId(), step.getId(), next);
        processTransitionRepository.save(transition);
    }

    private void saveSubtasksStep(ProcessGraph graph, Step step, SubtasksStepData data) {
        saveStep(data.getNext(), graph);

        CommonTransition transition = new CommonTransition(step.getProcessId(), step.getId(),
                data.getNext());
        commonTransitionRepository.save(transition);

        data.getSubtasks().stream()
                .map(subtask -> new Substep(subtask.id(), step, subtask.duration(), subtask.description()))
                .forEach(substepRepository::save);
    }

    @Override
    public ProcessGraph loadTemplate(UUID id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));

        List<Step> steps = stepRepository.findAllByProcessId(process.getId());
        Map<Integer, ProcessGraphNode> nodesMap = steps.stream()
                .map(step -> ProcessGraphNode.builder()
                        .id(step.getId())
                        .type(step.getType())
                        .duration(step.getDuration())
                        .metaInfo(step.getMetaInfo())
                        .description(step.getDescription())
                        .build())
                .collect(Collectors.toMap(ProcessGraphNode::getId, node -> node));

        resolveData(steps, nodesMap);
        return processGraphService.buildGraph(process.getName(), nodesMap.values().stream().toList(),
                process.getTotalDuration());
    }

    private void resolveData(List<Step> steps, Map<Integer, ProcessGraphNode> nodesMap) {
        for (Step step : steps) {
            ProcessGraphNode node = nodesMap.get(step.getId());
            resolveNodeData(step, node);
        }
    }

    private void resolveNodeData(Step step, ProcessGraphNode node) {
        StepType type = StepType.of(node.getType());
        StepData data = switch (type) {
            case COMMON -> {
                CommonTransition transition = commonTransitionRepository.findById(
                        new TransitionId(node.getId(), step.getProcessId())).orElseThrow();
                yield new CommonStepData(transition.getNextStepId());
            }
            case SUBTASKS -> {
                List<Substep> substeps = substepRepository.findAllByStep(step);
                List<Subtask> subtasks = substeps.stream()
                        .map(substep -> new Subtask(substep.getId(), substep.getDescription(), substep.getDuration()))
                        .toList();
                CommonTransition transition = commonTransitionRepository.findById(
                        new TransitionId(node.getId(), step.getProcessId())).orElseThrow();
                yield new SubtasksStepData(subtasks, transition.getNextStepId());
            }
            case CONDITIONAL -> {
                ConditionalTransition transition = conditionalTransitionRepository.findById(
                        new TransitionId(node.getId(), step.getProcessId())).orElseThrow();
                yield new ConditionalStepData(
                        transition.getPositiveStepId(),
                        transition.getNegativeStepId()
                );
            }
            case FINAL -> {
                FinalType finalType = finalTypeRepository.findById(
                        new TransitionId(node.getId(), step.getProcessId())).orElseThrow();
                yield new FinalData(finalType.isSuccessful());
            }
            case TRANSITION -> {
                ProcessTransition transition = processTransitionRepository.findById(
                        new TransitionId(node.getId(), step.getProcessId())).orElseThrow();
                yield new ProcessTransitionStepData(transition.getProcess().getId());
            }
        };
        node.setData(data);
    }
}
