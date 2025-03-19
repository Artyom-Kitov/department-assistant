package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ru.nsu.dgi.department_assistant.domain.service.FileService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    @Override
    public Path getTemplatePath(String fileName, String mimeName) {
        return Paths.get(fileName + "." + mimeName);
    }

    @Override
    public File getTemplateFile(String fileName, String mimeName) throws IOException {
        return getTemplatePath(fileName, mimeName).toFile();
    }
}
