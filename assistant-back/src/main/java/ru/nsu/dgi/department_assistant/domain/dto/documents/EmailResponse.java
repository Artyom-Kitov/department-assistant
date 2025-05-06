package ru.nsu.dgi.department_assistant.domain.dto.documents;

public record EmailResponse(
        boolean success,
        String messageId,      // ID письма в Gmail
        String error           // Если success=false
) {}