package ru.nsu.dgi.department_assistant.domain.dto.document;

public record EmailTemplateDto(
        Integer id,
        String subject,
        String body,
        boolean isHtml
) {
}
