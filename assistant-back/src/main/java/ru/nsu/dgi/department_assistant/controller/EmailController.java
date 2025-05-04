package ru.nsu.dgi.department_assistant.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.EmailService;

/**
 * Контроллер для API почты
 */
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email API", description = "API для работы с почтой")
public class EmailController {
    
    private final EmailService emailService;
    
    @Operation(
            summary = "Отправить письмо",
            description = "Отправляет письмо с использованием Gmail API"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Письмо успешно отправлено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для отправки"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmailResponse> sendEmail(
            @RequestParam Long templateId,
            @RequestParam UUID employeeId,
            @RequestParam(required = false) List<Long> attachmentTemplateIds,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            EmailResponse response = emailService.sendEmail(templateId, employeeId, 
                attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), files);
            return ResponseEntity.ok(response);
        } catch (EmailServiceException e) {
            return ResponseEntity.badRequest().body(new EmailResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new EmailResponse(false, null, "Internal server error: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "Отправить несколько писем",
            description = "Отправляет несколько писем с использованием Gmail API"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Письма успешно отправлены"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для отправки"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PostMapping(value = "/send/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<EmailResponse>> sendBulkEmails(
            @RequestParam Long templateId,
            @RequestParam List<UUID> employeeIds,
            @RequestParam(required = false) List<Long> attachmentTemplateIds,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            List<EmailResponse> responses = emailService.sendBulk(templateId, employeeIds,
                attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), files);
            return ResponseEntity.ok(responses);
        } catch (EmailServiceException e) {
            return ResponseEntity.badRequest().body(List.of(
                new EmailResponse(false, null, e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(List.of(new EmailResponse(false, null, "Internal server error: " + e.getMessage())));
        }
    }
    
    @Operation(
            summary = "Создать черновик письма",
            description = "Создает черновик письма в Gmail"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Черновик успешно создан"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для создания черновика"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован"
                    )
            }
    )
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createDraft(
            @RequestParam Long templateId,
            @RequestParam UUID employeeId,
            @RequestParam(required = false) List<Long> attachmentTemplateIds,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            String draftId = emailService.createDraft(templateId, employeeId, 
                attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), files);
            return ResponseEntity.ok(draftId);
        } catch (EmailServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Internal server error: " + e.getMessage());
        }
    }
}