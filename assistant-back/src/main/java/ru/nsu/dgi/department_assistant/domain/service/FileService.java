package ru.nsu.dgi.department_assistant.domain.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    Path getTemplatePath(String fileName, String mimeName);
    File getTemplateFile(String fileName, String mimeName) throws IOException;
    byte[] convertToBytes(XWPFDocument document);
}