package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.documents.FileResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.FileEntity;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.StorageCreationFailureException;
import ru.nsu.dgi.department_assistant.domain.exception.StorageFileException;
import ru.nsu.dgi.department_assistant.domain.mapper.documents.FileEntityMapper;
import ru.nsu.dgi.department_assistant.domain.repository.documents.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${file.storage-path}")
    private Path rootLocation;
    private final FileEntityMapper fileEntityMapper;
    private final FileRepository fileRepository;

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageCreationFailureException("Could not initialize storage", e);
        }
    }

    public FileResponseDto storeFile(
            MultipartFile file,
            String fileName,
            String fileExtension,
            FileEntity.TemplateType templateType,
            String subjectText
    ) {
        try {
            String fsFileName = UUID.randomUUID().toString();
            String storageFileName = resolveFullFileName(fsFileName, fileExtension);

            Path destinationFile = rootLocation.resolve(storageFileName)
                    .normalize()
                    .toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            FileEntity entity = new FileEntity();
            entity.setFileName(fileName);
            entity.setFsFileName(fsFileName);
            entity.setFileExtension(fileExtension);
            entity.setSubjectText(subjectText);
            entity.setSize(file.getSize());
            entity.setUploadDate(LocalDateTime.now());
            entity.setTemplateType(templateType);

            entity = fileRepository.save(entity);

            return fileEntityMapper.toDto(entity);
        } catch (IOException e) {
            throw new StorageFileException("Failed to store a new file", e);
        }
    }

    public List<FileResponseDto> findAll() {
        return fileRepository.findAll().stream()
                .map(fileEntityMapper::toDto)
                .toList();
    }

    public FileResponseDto updateFile(
            Long id,
            MultipartFile newFile,
            String fileName,
            String fileExtension,
            FileEntity.TemplateType templateType,
            String subjectText
    ) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        try {
            Files.deleteIfExists(Path.of(resolveFullFileName(entity.getFsFileName(), fileExtension)));

            String fsFileName = UUID.randomUUID().toString();
            String storageFileName = resolveFullFileName(fsFileName, fileExtension);

            Path destinationFile = rootLocation.resolve(storageFileName)
                    .normalize()
                    .toAbsolutePath();

            Files.copy(newFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            entity.setFileName(fileName);
            entity.setFsFileName(fsFileName);
            entity.setFileExtension(fileExtension);
            entity.setSubjectText(subjectText);
            entity.setSize(newFile.getSize());
            entity.setUploadDate(LocalDateTime.now());
            entity.setTemplateType(templateType);

            entity = fileRepository.save(entity);

            return fileEntityMapper.toDto(entity);
        } catch (IOException e) {
            throw new StorageFileException("Failed to update file", e);
        }
    }

    public void deleteFile(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Files.deleteIfExists(Path.of(resolveFullFileName(entity.getFsFileName(), entity.getFileExtension())));
            fileRepository.delete(entity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public Path getFilePathByFileId(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        return rootLocation.resolve(resolveFullFileName(entity.getFsFileName(), entity.getFileExtension()));
    }

    public String getFileExtensionById(Long id){
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        return entity.getFileExtension();
    }

    private String resolveFullFileName(String fsFileName, String fileExtension) {
        return fsFileName + "." + fileExtension;
    }
}
