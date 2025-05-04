package ru.nsu.dgi.department_assistant.domain.dto.documents;

import java.util.List;
import java.util.UUID;

public record EmailRequest(
    Long templateId,        // ID шаблона письма
    UUID employeeId,        // ID сотрудника (для заполнения переменных)
    List<Long> attachmentTemplateIds  // ID шаблонов вложений (опционально)
) {
    public EmailRequest {
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (attachmentTemplateIds == null) {
            attachmentTemplateIds = List.of();
        }
    }
}