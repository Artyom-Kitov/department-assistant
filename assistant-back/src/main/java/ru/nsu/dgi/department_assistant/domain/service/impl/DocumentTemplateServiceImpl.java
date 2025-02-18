package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.repository.document.DocumentTemplateRepository;
import ru.nsu.dgi.department_assistant.domain.service.DocumentTemplateService;

import java.nio.file.Path;
import java.util.Map;

public class DocumentTemplateServiceImpl implements DocumentTemplateService {
    //private final DocumentTemplateRepository documentTemplateRepository;
    @Override
    public byte[] convertToBytes(XWPFDocument document) {
        return new byte[0];
    }

    @Override
    public XWPFDocument convertToDocument(byte[] data) {
        return null;
    }

    @Override
    public DocumentTemplate getTemplateById(Integer id) {
        return null;
    }

    @Override
    public DocumentTemplate saveTemplate(String title, XWPFDocument document) {
        return null;
    }

    @Override
    public XWPFDocument fillTemplate(DocumentTemplate template, Map<String, String> data) {
        return null;
    }

    @Override
    public Path saveGeneratedDocument(XWPFDocument document, String fileName) {
        return null;
    }

    @Override
    public void deleteTemplate(Integer id) {

    }
}
