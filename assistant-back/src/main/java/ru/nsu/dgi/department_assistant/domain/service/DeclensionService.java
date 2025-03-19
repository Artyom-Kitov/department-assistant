package ru.nsu.dgi.department_assistant.domain.service;

public interface DeclensionService {
    String declineName(String key, String value, String caseType);
}
