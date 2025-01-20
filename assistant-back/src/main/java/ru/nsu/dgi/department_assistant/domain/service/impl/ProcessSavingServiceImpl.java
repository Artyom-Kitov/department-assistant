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
import ru.nsu.dgi.department_assistant.domain.exception.ProcessNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ConditionalStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.FinalData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.ProcessTransitionStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
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
    public UUID saveTemplate(String name, int totalDuration, ProcessGraphNode root) {
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

        switch (node.getData()) {
            case CommonStepData data -> saveCommonStep(process, step, data);
            case ConditionalStepData data -> saveConditionalStep(process, step, data);
            case FinalData data -> saveFinal(step, data);
            case ProcessTransitionStepData data -> saveProcessTransition(step, data);
            case SubtasksStepData data -> saveSubtasksStep(process, step, data);
        }
    }

    private Step findStepById(UUID id) {
        return stepRepository.findById(id)
                .orElseThrow(() -> new InvalidProcessTemplateException("no step with id " + id));
    }

    private void saveCommonStep(Process process, Step step, CommonStepData data) {
        saveStep(process, data.getNext());

        Step nextStep = findStepById(data.getNext().getId());
        CommonTransition transition = new CommonTransition(step.getId(), step, nextStep);
        commonTransitionRepository.save(transition);
    }

    private void saveConditionalStep(Process process, Step step, ConditionalStepData data) {
        saveStep(process, data.getIfTrue());
        saveStep(process, data.getIfFalse());

        Step ifTrueStep = findStepById(data.getIfTrue().getId());
        Step ifFalseStep = findStepById(data.getIfFalse().getId());
        ConditionalTransition transition = new ConditionalTransition(step.getId(), step, ifTrueStep, ifFalseStep);
        conditionalTransitionRepository.save(transition);
    }

    private void saveFinal(Step step, FinalData data) {
        FinalType finalType = new FinalType(step.getId(), data.isSuccessful(), step);
        finalTypeRepository.save(finalType);
    }

    private void saveProcessTransition(Step step, ProcessTransitionStepData data) {
        Process next = processRepository.findById(data.getNextProcess())
                .orElseThrow(() -> new InvalidProcessTemplateException("no process with id " + data.getNextProcess()));
        ProcessTransition transition = new ProcessTransition(step.getId(), step, next);
        processTransitionRepository.save(transition);
    }

    private void saveSubtasksStep(Process process, Step step, SubtasksStepData data) {
        saveStep(process, data.getNext());

        Step nextStep = findStepById(data.getNext().getId());
        CommonTransition transition = new CommonTransition(step.getId(), step, nextStep);
        commonTransitionRepository.save(transition);

        data.getSubtasks().stream()
                .map(subtask -> new Substep(subtask.id(), step, subtask.duration(), subtask.description()))
                .forEach(substepRepository::save);
    }

    @Override
    public ProcessGraph loadTemplate(UUID id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new ProcessNotFoundException(id));
        // TODO: load process from DB
        return null;
    }
}
