package ru.nsu.dgi.department_assistant.domain.service;

import java.util.Map;
import java.util.UUID;

public interface TemplateProcessingService {
    String replaceWithCases(String text, Map<String, String> data);
}
