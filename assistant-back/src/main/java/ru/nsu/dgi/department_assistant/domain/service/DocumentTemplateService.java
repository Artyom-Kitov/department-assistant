package ru.nsu.dgi.department_assistant.domain.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;

import java.nio.file.Path;
import java.util.Map;

public interface DocumentTemplateService {

    byte[] convertToBytes(XWPFDocument document);
    XWPFDocument convertToDocument(byte[] data);
    DocumentTemplate getTemplateById(Integer id);
    DocumentTemplate saveTemplate(String title, XWPFDocument document);
    XWPFDocument fillTemplate(DocumentTemplate template, Map<String, String> data);
    Path saveGeneratedDocument(XWPFDocument document, String fileName);
    void deleteTemplate(Integer id);


}
