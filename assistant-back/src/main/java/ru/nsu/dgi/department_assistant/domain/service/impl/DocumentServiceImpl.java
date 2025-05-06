package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepId;
import ru.nsu.dgi.department_assistant.domain.exception.*;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.MissingDocumentsDto;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmployeeMissingDocumentsDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import ru.nsu.dgi.department_assistant.domain.repository.documents.DocumentTypeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.EmployeeAtProcessRepository;
import ru.nsu.dgi.department_assistant.domain.entity.process.EmployeeAtProcess;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final TemplateHandlerDispatcherServiceImpl templateProcessingService;
    private final FileServiceImpl fileService;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProcessRepository processRepository;
    private final StepRepository stepRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAtProcessRepository employeeAtProcessRepository;

    @Override
    public byte[] fillAndConvertTemplate(Long templateId, UUID employeeId) {
        Object document = templateProcessingService.processTemplate(templateId, employeeId);
        return fileService.convertToBytes(document);
    }

    @Override
    public byte[] fillAndConvertTemplateWithStepContext(Long templateId, StepExecutedDto stepContext) {
        Object document = templateProcessingService.processTemplate(templateId, stepContext.employeeId());
        return fileService.convertToBytes(document);
    }

    @Override
    @Transactional
    public DocumentTypeDto createDocumentType(DocumentTypeCreationRequestDto request) {
        DocumentType documentType = new DocumentType();
        documentType.setName(request.name());
        documentType = documentTypeRepository.save(documentType);
        return DocumentTypeDto.fromEntity(documentType);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTypeDto getDocumentType(Long id) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new DocumentTypeNotFoundException("Document type not found with id: " + id));
        return DocumentTypeDto.fromEntity(documentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeDto> getAllDocumentTypes() {
        return documentTypeRepository.findAll().stream()
                .map(DocumentTypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentTypeDto updateDocumentType(Long id, DocumentTypeCreationRequestDto request) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new DocumentTypeNotFoundException("Document type not found with id: " + id));
        documentType.setName(request.name());
        documentType = documentTypeRepository.save(documentType);
        return DocumentTypeDto.fromEntity(documentType);
    }

    @Override
    @Transactional
    public void deleteDocumentType(Long id) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new DocumentTypeNotFoundException("Document type not found with id: " + id));
        
        if (documentTypeRepository.isDocumentTypeInUse(id)) {
            throw new DocumentTypeInUseException("Cannot delete document type that is in use");
        }
        
        documentTypeRepository.delete(documentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissingDocumentsDto> getMissingDocumentsForStep(UUID processId, int stepId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ProcessNotFoundException("Process not found with id: " + processId));

        if (!stepRepository.existsById(new StepId(stepId, processId))) {
            throw new StepNotFoundException("Step not found with id: " + stepId + " in process: " + processId);
        }

        List<Employee> employees = documentTypeRepository.findEmployeesForStep(processId, stepId);
        List<DocumentType> requiredDocuments = documentTypeRepository.findRequiredDocumentsForStep(processId, stepId);

        return employees.stream()
                .map(employee -> {
                    List<DocumentType> employeeDocuments = documentTypeRepository.findEmployeeDocumentsForStep(
                            employee.getId(), processId, stepId);
                    List<DocumentType> missingDocuments = requiredDocuments.stream()
                            .filter(doc -> !employeeDocuments.contains(doc))
                            .collect(Collectors.toList());
                    return MissingDocumentsDto.fromEntity(employee, missingDocuments.stream()
                            .map(DocumentTypeDto::fromEntity)
                            .collect(Collectors.toList()));
                })
                .filter(dto -> !dto.missingDocuments().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissingDocumentsDto> getMissingDocumentsForProcess(UUID processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ProcessNotFoundException("Process not found with id: " + processId));

        List<Employee> employees = documentTypeRepository.findEmployeesForProcess(processId);
        List<DocumentType> requiredDocuments = documentTypeRepository.findRequiredDocumentsForProcess(processId);

        return employees.stream()
                .map(employee -> {
                    List<DocumentType> employeeDocuments = documentTypeRepository.findEmployeeDocumentsForProcess(
                            employee.getId(), processId);
                    List<DocumentType> missingDocuments = requiredDocuments.stream()
                            .filter(doc -> !employeeDocuments.contains(doc))
                            .collect(Collectors.toList());
                    return MissingDocumentsDto.fromEntity(employee, missingDocuments.stream()
                            .map(DocumentTypeDto::fromEntity)
                            .collect(Collectors.toList()));
                })
                .filter(dto -> !dto.missingDocuments().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeMissingDocumentsDto> getEmployeeMissingDocuments(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        List<Process> processes = processRepository.findAllById(
            employeeAtProcessRepository.findByEmployeeId(employeeId)
                .stream()
                .map(EmployeeAtProcess::getProcessId)
                .collect(Collectors.toList())
        );

        return processes.stream()
                .map(process -> {
                    List<DocumentType> requiredDocuments = documentTypeRepository.findRequiredDocumentsForProcess(process.getId());
                    List<DocumentType> employeeDocuments = documentTypeRepository.findEmployeeDocumentsForProcess(
                            employee.getId(), process.getId());
                    List<DocumentType> missingDocuments = requiredDocuments.stream()
                            .filter(doc -> !employeeDocuments.contains(doc))
                            .collect(Collectors.toList());
                    return EmployeeMissingDocumentsDto.fromEntity(process, missingDocuments.stream()
                            .map(DocumentTypeDto::fromEntity)
                            .collect(Collectors.toList()));
                })
                .filter(dto -> !dto.missingDocuments().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeMissingDocumentsDto> getEmployeeMissingDocumentsForProcess(UUID employeeId, UUID processId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new ProcessNotFoundException("Process not found with id: " + processId));

        List<DocumentType> requiredDocuments = documentTypeRepository.findRequiredDocumentsForProcess(processId);
        List<DocumentType> employeeDocuments = documentTypeRepository.findEmployeeDocumentsForProcess(
                employee.getId(), processId);
        List<DocumentType> missingDocuments = requiredDocuments.stream()
                .filter(doc -> !employeeDocuments.contains(doc))
                .collect(Collectors.toList());

        return List.of(EmployeeMissingDocumentsDto.fromEntity(process, missingDocuments.stream()
                .map(DocumentTypeDto::fromEntity)
                .collect(Collectors.toList())));
    }
}



