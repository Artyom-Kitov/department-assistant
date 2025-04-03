package ru.nsu.dgi.department_assistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.impl.DocumentServiceImpl;

import java.util.UUID;



@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

        private final DocumentServiceImpl documentService;

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
                @RequestParam UUID templateId,
                @RequestParam UUID employeeId) {
            byte[] bytes = documentService.fillAndConvertTemplate(templateId,employeeId);
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.docx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
    }

