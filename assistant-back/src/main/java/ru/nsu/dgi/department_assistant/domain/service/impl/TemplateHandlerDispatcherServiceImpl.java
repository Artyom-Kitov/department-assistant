package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;
import ru.nsu.dgi.department_assistant.domain.service.MapBuilderService;
import ru.nsu.dgi.department_assistant.domain.service.TemplateHandlerDispatcherService;
import ru.nsu.dgi.department_assistant.domain.service.factory.TemplateHandlerFactory;
import ru.nsu.dgi.department_assistant.domain.service.handler.TemplateHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemplateHandlerDispatcherServiceImpl implements TemplateHandlerDispatcherService {

    private final TemplateHandlerFactory templateHandlerFactory;
    private final EmployeeService employeeService;
    private final MapBuilderService mapBuilderService;
    private final FileStorageService fileStorageService;
    private final FileServiceImpl fileService;

    public <T> T processTemplate(Long templateId, UUID employeeId) {
        Path template = fileStorageService.getFilePathByFileId(templateId);
        EmployeeWithAllInfoResponseDto employee = employeeService.getEmployeeWithAllInfos(employeeId);
        Map<String, String> data = mapBuilderService.buildMapForPerson(employee);


        byte[] templateBytes;
        try {
            templateBytes = fileService.getTemplateFileBytes(template);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template file", e);
        }

        TemplateHandler<T> handler = templateHandlerFactory.getHandler(fileStorageService.getFileExtensionById(templateId));

        try (InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            return handler.handleTemplate(inputStream, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process template", e);
        }
    }
}