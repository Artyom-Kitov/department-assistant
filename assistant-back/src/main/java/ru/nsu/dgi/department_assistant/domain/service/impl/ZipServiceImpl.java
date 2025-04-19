package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.exception.FileServiceException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZipServiceImpl {
    private final TemplateHandlerDispatcherServiceImpl templateHandlerDispatcherService;

    public byte[] createZipArchive(List<Long> templateIds, UUID employeeId, List<MultipartFile> uploadedFiles) throws IOException {
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


    public void addFileToZip(ZipOutputStream zos, String filename, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }


    public void addTemplateToZip(ZipOutputStream zos, Long templateId, UUID employeeId) throws IOException {
        String processedContent = templateHandlerDispatcherService.processTemplate(templateId, employeeId);
        addFileToZip(zos, templateId + ".txt", processedContent.getBytes());
    }


    public void addUploadedFileToZip(ZipOutputStream zos, MultipartFile file) throws IOException {
        addFileToZip(zos, file.getOriginalFilename(), file.getBytes());
    }
}

