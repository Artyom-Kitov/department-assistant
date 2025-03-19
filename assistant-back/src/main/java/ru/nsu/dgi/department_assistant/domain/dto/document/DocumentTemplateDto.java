package ru.nsu.dgi.department_assistant.domain.dto.document;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.*;
import ru.nsu.dgi.department_assistant.config.TemplateType;

import java.util.UUID;


public record DocumentTemplateDto(
    UUID id,
    String fileName,
    String mimeName,
    TemplateType templateType,
    String subject,
    String description

)
{}
