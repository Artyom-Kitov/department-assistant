package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.dgi.department_assistant.domain.service.impl.DocumentServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.FileStorageService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents/generate")
@RequiredArgsConstructor
public class DocumentGeneratorController {

    private final DocumentServiceImpl documentService;
    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Заполнить шаблон данными сотрудника",
            description = "Заполняет шаблон данными сотрудника и возвращает документ"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Документ успешно заполнен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для заполнения"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Шаблон или сотрудник с указанным ID не найден"
                    )
            }
    )
    @PostMapping("/fill")
    public ResponseEntity<Resource> fillTemplate(
            @RequestParam Long templateId,
            @RequestParam UUID employeeId) {
        byte[] bytes = documentService.fillAndConvertTemplate(templateId, employeeId);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = fileStorageService.getFileNameById(templateId) + "." + fileStorageService.getFileExtensionById(templateId);
        String asciiFilename = filename.replaceAll("[^\\x20-\\x7E]", "_");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + asciiFilename + "\"; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
//
//    @Operation(
//            summary = "Заполнить шаблон с контекстом шага процесса",
//            description = "Заполняет шаблон данными сотрудника и контекстом шага процесса"
//    )
//    @ApiResponses(
//            value = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "Документ успешно заполнен с контекстом шага"
//                    ),
//                    @ApiResponse(
//                            responseCode = "400",
//                            description = "Неверные данные для заполнения"
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Шаблон, сотрудник или данные шага не найдены"
//                    )
//            }
//    )
//    @PostMapping("/fill-with-step")
//    public ResponseEntity<Resource> generateDocumentWithStep(
//            @RequestParam Long templateId,
//            @RequestBody StepExecutedDto stepContext) {
//        byte[] bytes = documentService.fillAndConvertTemplateWithStepContext(templateId, stepContext);
//        ByteArrayResource resource = new ByteArrayResource(bytes);
//        String filename = fileStorageService.getFileNameById(templateId) + "." + fileStorageService.getFileExtensionById(templateId);
//        String asciiFilename = filename.replaceAll("[^\\x20-\\x7E]", "_");
//        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + asciiFilename + "\"; filename*=UTF-8''" + encodedFilename)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
} 