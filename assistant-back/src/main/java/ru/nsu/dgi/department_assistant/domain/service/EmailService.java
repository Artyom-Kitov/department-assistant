package ru.nsu.dgi.department_assistant.domain.service;

import java.util.List;
import java.util.UUID;

import jakarta.mail.internet.MimeMessage;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;

public interface EmailService {
    /**
     * Creates a MIME message from the email request
     * @param templateId ID шаблона письма
     * @param employeeId ID сотрудника (для заполнения переменных)
     * @param attachmentTemplateIds ID шаблонов вложений (опционально)
     * @param files Optional list of files to attach
     * @return The created MIME message
     */
    MimeMessage createMimeMessage(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files);

    /**
     * Sends an email using the Gmail API
     * @param templateId ID шаблона письма
     * @param employeeId ID сотрудника (для заполнения переменных)
     * @param attachmentTemplateIds ID шаблонов вложений (опционально)
     * @param files Optional list of files to attach
     * @return EmailResponse containing the message ID and status
     */
    EmailResponse sendEmail(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files);

    /**
     * Sends multiple emails in bulk using the same template
     * @param templateId ID шаблона письма (один для всех писем)
     * @param employeeIds List of employee IDs
     * @param attachmentTemplateIds ID шаблонов вложений (опционально)
     * @param files Optional list of files to attach to all emails
     * @return List of email responses
     */
    List<EmailResponse> sendBulk(Long templateId, List<UUID> employeeIds, List<Long> attachmentTemplateIds, List<MultipartFile> files);

    /**
     * Creates a draft email
     * @param templateId ID шаблона письма
     * @param employeeId ID сотрудника (для заполнения переменных)
     * @param attachmentTemplateIds ID шаблонов вложений (опционально)
     * @param files Optional list of files to attach
     * @return The ID of the created draft
     */
    String createDraft(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files);
}
