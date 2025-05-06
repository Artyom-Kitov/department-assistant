package ru.nsu.dgi.department_assistant.domain.service;

import java.util.List;
import java.util.UUID;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.MissingDocumentsDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmployeeMissingDocumentsDto;

public interface DocumentService {

    DocumentTypeDto createDocumentType(DocumentTypeCreationRequestDto request);
    DocumentTypeDto getDocumentType(Long id);
    List<DocumentTypeDto> getAllDocumentTypes();
    DocumentTypeDto updateDocumentType(Long id, DocumentTypeCreationRequestDto request);
    void deleteDocumentType(Long id);
    
    byte[] fillAndConvertTemplate(Long templateId, UUID employeeId);
    byte[] fillAndConvertTemplateWithStepContext(Long templateId, StepExecutedDto stepContext);

    // Методы для анализа документов по процессу
    List<MissingDocumentsDto> getMissingDocumentsForStep(UUID processId, int stepId);
    List<MissingDocumentsDto> getMissingDocumentsForProcess(UUID processId);

    // Методы для анализа документов по сотруднику
    List<EmployeeMissingDocumentsDto> getEmployeeMissingDocuments(UUID employeeId);
    List<EmployeeMissingDocumentsDto> getEmployeeMissingDocumentsForProcess(UUID employeeId, UUID processId);
}
