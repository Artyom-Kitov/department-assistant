package ru.nsu.dgi.department_assistant.domain.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;

import java.nio.file.Path;
import java.util.Map;

public interface DocumentService {

    byte[] convertToBytes(XWPFDocument document);
    XWPFDocument convertToDocument(byte[] data);
    DocumentTemplateDto getTemplateById(Integer id);
    DocumentTemplateDto updateTemplate(Integer id, DocumentTemplateDto documentTemplateDto);
    DocumentTemplateDto saveTemplate(String title,MultipartFile file);
    XWPFDocument fillTemplate(DocumentTemplateDto template, Map<String, String> data);
    Map<String,String> buildMapForPerson(EmployeeWithAllInfoResponseDto employee);
    void deleteTemplate(Integer id);

}
