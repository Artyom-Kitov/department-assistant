package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.repository.document.DocumentTemplateRepository;
import ru.nsu.dgi.department_assistant.domain.service.DocumentTemplateService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class DocumentTemplateServiceImpl implements DocumentTemplateService {
    private final DocumentTemplateRepository documentTemplateRepository;
    // Конвертация XWPFDocument → byte[]
    @Override
    public byte[] convertToBytes(XWPFDocument document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении шаблона документа", e);
        }
    }

    // Конвертация byte[] → XWPFDocument
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
    public void saveTemplateFromOutside(MultipartFile file) {

    }

    @Override
    public XWPFDocument fillTemplate(DocumentTemplate template, Map<String, String> data) {
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
    public Map<String, String> buildMapForPerson(EmployeeResponseDto employeeResponseDto) {
        return null;
    }
    // add map generating function or add it in bd
    // add padegi

    @Override
    public Path saveGeneratedDocument(XWPFDocument document, String fileName) {
        Path filePath = Paths.get("/generated-docs/", fileName + ".docx"); //change path afterwards!!!

        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении документа", e);
        }
        return filePath;
    }

    @Override
    public void deleteTemplate(Integer id) {
        if (!documentTemplateRepository.existsById(id)) {
            throw new RuntimeException("Шаблон не найден");
        }
        documentTemplateRepository.deleteById(id);
    }
}

// add exceptions

