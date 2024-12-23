package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.entity.process.CommonTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.ConditionalTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.FinalType;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.ProcessTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.Substep;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.graph.CommonStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.ConditionalStepNode;
import ru.nsu.dgi.department_assistant.domain.graph.FinalNode;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessTransitionNode;
import ru.nsu.dgi.department_assistant.domain.graph.SubtasksStepNode;
import ru.nsu.dgi.department_assistant.domain.repository.process.CommonTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ConditionalTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.FinalTypeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;

import java.util.UUID;

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

    @Transactional
    @Override
    public UUID saveTemplateToDb(String name, int totalDuration, ProcessGraphNode root) {
        UUID id = UUID.randomUUID();
        Process process = new Process(id, name, totalDuration);
        processRepository.save(process);
        saveStep(process, root);
        return id;
    }

    private void saveStep(Process process, ProcessGraphNode node) {
        Step step = new Step(node.getId(), process, node.getDuration(), node.getMetaInfo(), node.getType(),
                node.getDescription());
        step = stepRepository.save(step);

        switch (node) {
            case CommonStepNode n -> saveCommonStep(process, step, n);
            case ConditionalStepNode n -> saveConditionalStep(process, step, n);
            case FinalNode n -> saveFinal(step, n);
            case ProcessTransitionNode n -> saveProcessTransition(step, n);
            case SubtasksStepNode n -> saveSubtasksStep(process, step, n);
        }
    }

    private Step findStepById(UUID id) {
        return stepRepository.findById(id)
                .orElseThrow(() -> new InvalidProcessTemplateException("no step with id " + id));
    }

    private void saveCommonStep(Process process, Step step, CommonStepNode node) {
        saveStep(process, node.getNext());

        Step nextStep = findStepById(node.getNext().getId());
        CommonTransition transition = new CommonTransition(step.getId(), step, nextStep);
        commonTransitionRepository.save(transition);
    }

    private void saveConditionalStep(Process process, Step step, ConditionalStepNode node) {
        saveStep(process, node.getIfTrue());
        saveStep(process, node.getIfFalse());

        Step ifTrueStep = findStepById(node.getIfTrue().getId());
        Step ifFalseStep = findStepById(node.getIfFalse().getId());
        ConditionalTransition transition = new ConditionalTransition(step.getId(), step, ifTrueStep, ifFalseStep);
        conditionalTransitionRepository.save(transition);
    }

    private void saveFinal(Step step, FinalNode node) {
        FinalType finalType = new FinalType(step.getId(), node.isSuccessful(), step);
        finalTypeRepository.save(finalType);
    }

    private void saveProcessTransition(Step step, ProcessTransitionNode node) {
        Process next = processRepository.findById(node.getNextProcess())
                .orElseThrow(() -> new InvalidProcessTemplateException("no process with id " + node.getNextProcess()));
        ProcessTransition transition = new ProcessTransition(step.getId(), step, next);
        processTransitionRepository.save(transition);
    }

    private void saveSubtasksStep(Process process, Step step, SubtasksStepNode node) {
        saveStep(process, node.getNext());

        Step nextStep = findStepById(node.getNext().getId());
        CommonTransition transition = new CommonTransition(step.getId(), step, nextStep);
        commonTransitionRepository.save(transition);

        node.getSubtasks().stream()
                .map(subtask -> new Substep(subtask.id(), step, subtask.duration(), subtask.description()))
                .forEach(substepRepository::save);
    }
}
