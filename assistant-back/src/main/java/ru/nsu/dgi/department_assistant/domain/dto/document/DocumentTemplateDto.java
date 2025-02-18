package ru.nsu.dgi.department_assistant.domain.dto.document;
import org.apache.poi.xwpf.usermodel.*;

public record DocumentTemplateDto(
        Integer id,
        String title,
        byte[] templateData)
{}
