package ru.nsu.dgi.department_assistant.domain.dto.documents;

import java.time.LocalDateTime;

public record FileResponseDto(
        Long id,
        String fileName,
        String fileExtension,
        String templateType,
        Long size,
        LocalDateTime uploadDate,
        String subjectText
) {}
