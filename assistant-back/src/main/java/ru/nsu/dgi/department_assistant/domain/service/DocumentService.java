package ru.nsu.dgi.department_assistant.domain.service;
import java.util.UUID;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
public interface DocumentService {

    byte[] fillAndConvertTemplate(Long templateId, UUID employeeId);
    byte[] fillAndConvertTemplateWithStepContext(Long templateId, StepExecutedDto stepContext);

}
