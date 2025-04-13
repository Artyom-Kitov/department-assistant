package ru.nsu.dgi.department_assistant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailRequest;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
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
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest request) {
        EmailResponse response = emailService.sendEmail(request);
        return ResponseEntity.ok(response);
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
    @PostMapping("/send/bulk")
    public ResponseEntity<List<EmailResponse>> sendBulkEmails(@RequestBody List<EmailRequest> requests) {
        List<EmailResponse> responses = emailService.sendBulk(requests);
        return ResponseEntity.ok(responses);
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
    @PostMapping("/draft")
    public ResponseEntity<String> createDraft(@RequestBody EmailRequest request) {
        String draftId = emailService.createDraft(request);
        return ResponseEntity.ok(draftId);
    }
}