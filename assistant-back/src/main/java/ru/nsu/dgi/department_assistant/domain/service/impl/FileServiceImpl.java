package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.FileService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.exception.FileServiceException;
import ru.nsu.dgi.department_assistant.domain.service.TemplateHandlerDispatcherService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final TemplateHandlerDispatcherService templateHandlerDispatcherService;

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
        // TODO: Реализовать конвертацию HTML
        throw new UnsupportedOperationException("HTML conversion not implemented yet");
    }

    private byte[] convertTxtToBytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getTemplateFileBytes(Path filePath) throws IOException {
        return Files.readAllBytes(filePath); 
    }

    @Override
    public byte[] createZipArchive(List<Long> templateIds, Long employeeId, List<MultipartFile> uploadedFiles) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            // Add template files
            for (Long templateId : templateIds) {
                addTemplateToZip(zos, templateId, employeeId);
            }
            
            // Add uploaded files
            for (MultipartFile file : uploadedFiles) {
                addUploadedFileToZip(zos, file);
            }
            
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to create zip archive", e);
            throw new FileServiceException("Failed to create zip archive", e);
        }
    }

    @Override
    public void addFileToZip(ZipOutputStream zos, String filename, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }

    @Override
    public void addTemplateToZip(ZipOutputStream zos, Long templateId, Long employeeId) throws IOException {
        String processedContent = templateHandlerDispatcherService.processTemplate(templateId, employeeId);
        addFileToZip(zos, templateId + ".txt", processedContent.getBytes());
    }

    @Override
    public void addUploadedFileToZip(ZipOutputStream zos, MultipartFile file) throws IOException {
        addFileToZip(zos, file.getOriginalFilename(), file.getBytes());
    }
}
