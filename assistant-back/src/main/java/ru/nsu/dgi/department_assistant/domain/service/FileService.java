package ru.nsu.dgi.department_assistant.domain.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    byte[] createZipArchive(List<Long> templateIds, Long employeeId, List<MultipartFile> uploadedFiles) throws IOException;
    void addFileToZip(ZipOutputStream zos, String filename, byte[] content) throws IOException;
    void addTemplateToZip(ZipOutputStream zos, Long templateId, Long employeeId) throws IOException;
    void addUploadedFileToZip(ZipOutputStream zos, MultipartFile file) throws IOException;
     byte[] convertToBytes(Object document);
}