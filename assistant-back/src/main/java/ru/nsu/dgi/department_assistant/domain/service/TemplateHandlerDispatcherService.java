package ru.nsu.dgi.department_assistant.domain.service;

import java.util.UUID;

public interface TemplateHandlerDispatcherService {
    <T> T processTemplate(Long templateId, UUID employeeId);
}
