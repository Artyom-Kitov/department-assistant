package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.documents.FileResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.FileEntity;
import ru.nsu.dgi.department_assistant.domain.service.impl.FileStorageService;

import java.util.List;

@Tag(name = "File storage", description = "A controller for file manipulations inside a server storage.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileStorageController {
    private final FileStorageService storageService;

    @PostMapping(value = "/store", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> upload(
            @RequestParam MultipartFile file,
            @RequestParam String fileName,
            @RequestParam String fileExtension,
            @RequestParam FileEntity.TemplateType templateType,
            @RequestParam(required = false) String subjectText
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageService.storeFile(file, fileName, fileExtension, templateType, subjectText));
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> update(
            @RequestParam Long id,
            @RequestParam MultipartFile file,
            @RequestParam String fileName,
            @RequestParam String fileExtension,
            @RequestParam FileEntity.TemplateType templateType,
            @RequestParam(required = false) String subjectText
    ) {
        return ResponseEntity.ok(storageService.updateFile(
                id, file, fileName, fileExtension, templateType, subjectText));
    }

    @GetMapping("/get-all")
    public List<FileResponseDto> getAll() {
        return storageService.findAll();
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam Long id) {
        storageService.deleteFile(id);
    }
}
