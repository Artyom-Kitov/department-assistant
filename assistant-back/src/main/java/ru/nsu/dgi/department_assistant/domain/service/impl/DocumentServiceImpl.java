package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class DocumentServiceImpl implements DocumentService {
    private final TemplateHandlerDispatcherServiceImpl templateProcessingService;
    private final FileServiceImpl fileService;

    private final FileStorageService fileStorageService;

    @Override
    public byte[] fillAndConvertTemplate(Long templateId, UUID employeeId){
    Object document = templateProcessingService.processTemplate(templateId, employeeId);

    return fileService.convertToBytes(document);
}

@Override
public byte[] fillAndConvertTemplateWithStepContext(Long templateId, StepExecutedDto stepContext) {
    // Получаем документ с базовым контекстом сотрудника
    Object document = templateProcessingService.processTemplate(templateId, stepContext.employeeId());
    
    return fileService.convertToBytes(document);
}

}



