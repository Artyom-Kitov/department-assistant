package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.FileService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public byte[] convertToBytes(XWPFDocument document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении шаблона документа", e);
        }
    }

    public byte[] getTemplateFileBytes(Path filePath) throws IOException {
        return Files.readAllBytes(filePath); 
    }

}
