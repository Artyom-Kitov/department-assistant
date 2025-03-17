package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.mapper.document.DocumentTemplateMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.AcademicDegreeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.document.DocumentTemplateRepository;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class DocumentServiceImpl implements DocumentService {
    private final DocumentTemplateRepository documentTemplateRepository;
    private final DocumentTemplateMapper documentTemplateMapper;

    @Override
    public byte[] convertToBytes(XWPFDocument document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении шаблона документа", e);
        }
    }

    @Override
    public XWPFDocument convertToDocument(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            return new XWPFDocument(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке шаблона документа", e);
        }
    }

    @Override
    public DocumentTemplateDto getTemplateById(Integer id) {
        DocumentTemplate template = documentTemplateRepository.findById(id).orElseThrow();
        return new DocumentTemplateDto(template.getId(), template.getTitle(), template.getTemplateData());
    }

    @Override
    public DocumentTemplateDto updateTemplate(Integer id, DocumentTemplateDto documentTemplateDto) {
        return null;
    }

    @Override
    public DocumentTemplateDto saveTemplate(String title, MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            DocumentTemplateDto templateDTO = new DocumentTemplateDto(null, title, fileBytes);
            DocumentTemplate template = documentTemplateMapper.toEntity(templateDTO);
            template = documentTemplateRepository.save(template);
            return documentTemplateMapper.toDTO(template);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    @Override
    public XWPFDocument fillTemplate(DocumentTemplateDto documentTemplate, Map<String, String> data) {
        DocumentTemplate template = documentTemplateMapper.toEntity(documentTemplate);
        XWPFDocument document = convertToDocument(template.getTemplateData());

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
                    }
                    run.setText(text, 0);
                }
            }
        }
        return document;
    }

    @Override
    public Map<String, String> buildMapForPerson(EmployeeWithAllInfoResponseDto employee) {
        Map<String, String> dataMap = new HashMap<>();

        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-YYYY");
        String date = formatter.format(current);
        dataMap.put("date", date);

        for (Field field : EmployeeWithAllInfoResponseDto.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(employee);
                dataMap.put(field.getName(), value != null ? value.toString() : "");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля: " + field.getName(), e);
            }
        }

        return dataMap;
    }
    // add padegi



    @Override
    @Transactional
    public void deleteTemplate(Integer id) {
        if (!documentTemplateRepository.existsById(id)) {
            throw new RuntimeException("Шаблон не найден");
        }
        documentTemplateRepository.deleteById(id);
    }
}

// add exceptions

