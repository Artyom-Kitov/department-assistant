package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.FileService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {
    private final Path tempDirectory = Paths.get("temp-documents");
    @Override
    public byte[] convertToBytes(XWPFDocument document) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении шаблона документа", e);
        }
    }

    public byte[] getTemplateFileBytes(String fileName, String mimeName) throws IOException {
        Path filePath = Paths.get("templates", fileName + "." + mimeName); // Путь к файлу шаблона
        return Files.readAllBytes(filePath); // Чтение файла в массив байтов
    }

//    public void deleteFileById(String fileId, String extension) {
//        Path filePath = tempDirectory.resolve(fileId + "." + extension);
//
//        try {
//            Files.deleteIfExists(filePath); // Удаляем файл
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete file", e);
//        }
//    }
//    private void deleteFile(Path filePath) {
//        try {
//            Files.deleteIfExists(filePath); // Удаляем файл по пути
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete file: " + filePath, e);
//        }
//    }
//
//    @Scheduled(fixedRate = 60 * 60 * 1000) // Очистка каждый час
//    public void cleanUpOldFiles() {
//        try {
//            Files.walk(tempDirectory)
//                    .filter(Files::isRegularFile)
//                    .filter(this::isExpired)
//                    .forEach(this::deleteFile);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to clean up old files", e);
//        }
//    }
//
//    private boolean isExpired(Path filePath) {
//        try {
//            return Files.getLastModifiedTime(filePath).toMillis() < System.currentTimeMillis() - 60 * 60 * 1000; // Старые файлы (1 час)
//        } catch (IOException e) {
//            return false;
//        }
//    }
    @Override
    public Path getTemplatePath(String fileName, String mimeName) {
        return Paths.get(fileName + "." + mimeName);
    }

    @Override
    public File getTemplateFile(String fileName, String mimeName) throws IOException {
        return getTemplatePath(fileName, mimeName).toFile();
    }
}
