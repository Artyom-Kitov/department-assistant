package ru.nsu.dgi.department_assistant.domain.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.document.DocumentTemplateMapper;
import ru.nsu.dgi.department_assistant.domain.repository.document.DocumentTemplateRepository;
import ru.nsu.dgi.department_assistant.domain.service.DocumentTemplateService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTemplateServiceImpl implements DocumentTemplateService {
    private final DocumentTemplateRepository templateRepository;
    private final DocumentTemplateMapper templateMapper;

    @Override
    public DocumentTemplateDto getTemplateById(UUID id) {
        DocumentTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Шаблон с ID " + id + " не найден"));
        return templateMapper.toDto(entity);
    }

    @Override
    public DocumentTemplateDto createTemplate(DocumentTemplateDto templateDto) {
        DocumentTemplate entity = templateMapper.toEntity(templateDto);
        DocumentTemplate savedEntity = templateRepository.save(entity);
        return templateMapper.toDto(savedEntity);
    }

    @Override
    public DocumentTemplateDto updateTemplate(UUID id, DocumentTemplateDto templateDto) {
        DocumentTemplate existingEntity = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Шаблон с ID " + id + " не найден"));

        // Обновляем поля
        existingEntity.setFileName(templateDto.fileName());
        existingEntity.setMimeName(templateDto.mimeName());
        existingEntity.setTemplateType(templateDto.templateType());
        existingEntity.setSubject(templateDto.subject());
        existingEntity.setDescription(templateDto.description());


        DocumentTemplate updatedEntity = templateRepository.save(existingEntity);
        return templateMapper.toDto(updatedEntity);
    }


    @Override
    public void deleteTemplate(UUID id) {
        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException("Шаблон с ID " + id + " не найден");
        }
        templateRepository.deleteById(id);
    }

}

