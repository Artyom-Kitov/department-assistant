package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.FileService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {


    @Override
    public byte[] convertToBytes(Object document) {
        return switch (document) {
            case XWPFDocument doc -> convertDocxToBytes(doc);
            case String text -> convertTxtToBytes(text);
            case null -> throw new IllegalArgumentException("Document cannot be null");
            default -> throw new UnsupportedOperationException("Unsupported document type: " + document.getClass());
        };
    }

    private byte[] convertDocxToBytes(XWPFDocument document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении DOCX документа", e);
        }
    }

    private byte[] convertHtmlToBytes(String html) {
        return html.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] convertTxtToBytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getTemplateFileBytes(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }


}
