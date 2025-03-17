package ru.nsu.dgi.department_assistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;

public class DocumentController {
    private DocumentService documentTemplateService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentTemplateDto> uploadTemplate(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {

        DocumentTemplateDto savedTemplate = documentTemplateService.saveTemplate(title, file);
        return ResponseEntity.ok(savedTemplate);
    }
}
