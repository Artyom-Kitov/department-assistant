package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;
import ru.nsu.dgi.department_assistant.domain.service.MapBuilderService;
import ru.nsu.dgi.department_assistant.domain.service.TemplateHandlerDispatcherService;
import ru.nsu.dgi.department_assistant.domain.service.factory.TemplateHandlerFactory;
import ru.nsu.dgi.department_assistant.domain.service.handler.TemplateHandler;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemplateHandlerDispatcherServiceImpl implements TemplateHandlerDispatcherService {

    private final TemplateHandlerFactory templateHandlerFactory;
    private final EmployeeService employeeService;
    private final MapBuilderService mapBuilderService;
    private final DocumentTemplateServiceImpl documentTemplateService;
    private final FileServiceImpl fileService;

    public <T> T processTemplate(UUID templateId, UUID employeeId) {
        // Получаем шаблон и данные сотрудника
        DocumentTemplateDto template = documentTemplateService.getTemplateById(templateId);
        EmployeeWithAllInfoResponseDto employee = employeeService.getEmployeeWithAllInfos(employeeId);
        Map<String, String> data = mapBuilderService.buildMapForPerson(employee);

        // Получаем файл шаблона
        byte[] templateBytes;
        try {
            templateBytes = fileService.getTemplateFileBytes(template.fileName(), template.mimeName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template file", e);
        }
        // Выбираем стратегию
        TemplateHandler<T> handler = templateHandlerFactory.getHandler(template.templateType());

        // Обрабатываем шаблон
        try (InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            return handler.handleTemplate(inputStream, data); // Работаем с данными в памяти
        } catch (IOException e) {
            throw new RuntimeException("Failed to process template", e);
        }
    }
}