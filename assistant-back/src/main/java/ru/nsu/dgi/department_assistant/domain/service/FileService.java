package ru.nsu.dgi.department_assistant.domain.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface FileService {
    byte[] convertToBytes(XWPFDocument document);
}