package ru.nsu.dgi.department_assistant.domain.service;

import java.util.Map;

public interface TemplateProcessingService {
    /**
     * Replaces variables in text with their values from the data map
     * @param text The text containing variables to replace
     * @param data Map of variable names to their values
     * @return The processed text with variables replaced
     */
    String replaceWithCases(String text, Map<String, String> data);


}
