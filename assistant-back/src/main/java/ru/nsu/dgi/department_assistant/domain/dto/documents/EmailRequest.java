package ru.nsu.dgi.department_assistant.domain.dto.documents;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public record EmailRequest(
    Long templateId,        // ID шаблона письма
    UUID employeeId,        // ID сотрудника (для заполнения переменных)
    List<Long> attachmentTemplateIds,  // ID шаблонов вложений (опционально)
    List<MultipartFile> uploadedFiles  // Загруженные файлы (опционально)
) {}