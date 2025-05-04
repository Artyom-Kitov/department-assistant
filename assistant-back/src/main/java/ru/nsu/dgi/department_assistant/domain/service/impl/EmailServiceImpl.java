package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailRequest;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.FileEntity;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final TemplateHandlerDispatcherService templateHandlerDispatcherService;
    private final GmailApiService gmailApiService;
    private final ContactsService contactsService;
    private final SecurityService securityService;
    private final ZipServiceImpl zipService;
    private final FileStorageService fileStorageService;

    @Override
    public EmailResponse sendEmail(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files) {
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        // Check if template is of type EMAIL
        FileEntity.TemplateType templateType = fileStorageService.getFileTemplateTypeById(templateId);
        if (templateType != FileEntity.TemplateType.EMAIL) {
            throw new EmailServiceException("Template with ID " + templateId + " is not an email template. Template type: " + templateType);
        }

        try {
            MimeMessage message = createMimeMessage(templateId, employeeId, 
                attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), 
                files != null ? files : List.of());
            return gmailApiService.sendEmail(message);
        } catch (EmailServiceException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while sending email", e);
            throw new EmailServiceException("Failed to send email: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<EmailResponse> sendBulk(Long templateId, List<UUID> employeeIds, List<Long> attachmentTemplateIds, List<MultipartFile> files) {
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeIds == null) {
            throw new EmailServiceException("Employee IDs list cannot be null");
        }
        if (employeeIds.isEmpty()) {
            throw new EmailServiceException("Employee IDs list cannot be empty");
        }

        List<EmailResponse> responses = new ArrayList<>();
        for (UUID employeeId : employeeIds) {
            try {
                responses.add(sendEmail(templateId, employeeId, 
                    attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), 
                    files != null ? files : List.of()));
            } catch (EmailServiceException e) {
                log.error("Failed to send email to employee {} with template {}", employeeId, templateId, e);
                responses.add(new EmailResponse(false, null, e.getMessage()));
            }
        }
        return responses;
    }
    
    @Override
    public String createDraft(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files) {
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        // Check if template is of type EMAIL
        FileEntity.TemplateType templateType = fileStorageService.getFileTemplateTypeById(templateId);
        if (templateType != FileEntity.TemplateType.EMAIL) {
            throw new EmailServiceException("Template with ID " + templateId + " is not an email template. Template type: " + templateType);
        }

        try {
            MimeMessage message = createMimeMessage(templateId, employeeId, 
                attachmentTemplateIds != null ? attachmentTemplateIds : List.of(), 
                files != null ? files : List.of());
            return gmailApiService.createDraft(message);
        } catch (EmailServiceException e) {
            log.error("Failed to create draft: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating draft", e);
            throw new EmailServiceException("Failed to create draft: " + e.getMessage(), e);
        }
    }

    @Override
    public MimeMessage createMimeMessage(Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files) {
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            
            setMessageHeaders(message, employeeId);
            setMessageBody(message, templateId, employeeId);
            addAttachmentsIfNeeded(message, templateId, employeeId, attachmentTemplateIds, files);
            
            return message;
        } catch (EmailServiceException e) {
            log.error("Failed to create MIME message: {}", e.getMessage(), e);
            throw e;
        } catch (MessagingException | IOException e) {
            log.error("Unexpected error while creating MIME message", e);
            throw new EmailServiceException("Failed to create MIME message: " + e.getMessage(), e);
        }
    }

    private void setMessageHeaders(MimeMessage message, UUID employeeId) throws MessagingException {
        if (message == null) {
            throw new EmailServiceException("MimeMessage cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        String userEmail = securityService.getCurrentUserEmail();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new EmailServiceException("Current user email is not set");
        }

        ContactsResponseDto contacts = contactsService.getContactsByEmployeeId(employeeId);
        if (contacts == null) {
            throw new EmailServiceException("Contacts not found for employee: " + employeeId);
        }
        
        String recipientEmail = contacts.email();
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            throw new EmailServiceException("Recipient email is not set for employee: " + employeeId);
        }
        
        if (!recipientEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new EmailServiceException("Invalid email format for recipient: " + recipientEmail);
        }
        
        try {
            message.setFrom(userEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to set message headers", e);
            throw new EmailServiceException("Failed to set message headers: " + e.getMessage(), e);
        }
    }

    private String createAutomaticSignature() {
        return "<div style='text-align: center; margin-top: 2em; color: #666; font-size: 0.9em;'>" +
               "<hr style='width: 50%; margin: 1em auto;'>" +
               "Это письмо было сгенерировано автоматически" +
               "</div>";
    }

    private void setMessageBody(MimeMessage message, Long templateId, UUID employeeId) throws MessagingException {
        if (message == null) {
            throw new EmailServiceException("MimeMessage cannot be null");
        }
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        try {
            String processedBody = templateHandlerDispatcherService.processTemplate(
                templateId,
                employeeId
            );
            if (processedBody == null || processedBody.trim().isEmpty()) {
                throw new EmailServiceException("Processed template body is empty");
            }
            
            String subject = fileStorageService.getFileSubjectById(templateId);
            if (subject == null || subject.trim().isEmpty()) {
                throw new EmailServiceException("Email subject is not set for template: " + templateId);
            }
            
            message.setSubject(subject);
            
            String fileExtension = fileStorageService.getFileExtensionById(templateId);
            if ("html".equalsIgnoreCase(fileExtension)) {
                // Если это HTML шаблон, добавляем подпись перед закрывающим тегом body
                int bodyEndIndex = processedBody.lastIndexOf("</body>");
                if (bodyEndIndex != -1) {
                    String htmlContent = processedBody.substring(0, bodyEndIndex) + 
                                       createAutomaticSignature() + 
                                       processedBody.substring(bodyEndIndex);
                    message.setContent(htmlContent, "text/html; charset=UTF-8");
                } else {
                    // Если тег body не найден, просто добавляем подпись в конец
                    message.setContent(processedBody + createAutomaticSignature(), "text/html; charset=UTF-8");
                }
            } else {
                // Для обычного текста просто добавляем подпись в конец
                message.setText(processedBody + "\n\n---\nЭто письмо было сгенерировано автоматически");
            }
        } catch (Exception e) {
            log.error("Failed to set message body", e);
            throw new EmailServiceException("Failed to set message body: " + e.getMessage(), e);
        }
    }

    private void addAttachmentsIfNeeded(MimeMessage message, Long templateId, UUID employeeId, List<Long> attachmentTemplateIds, List<MultipartFile> files) throws MessagingException, IOException {
        if (message == null) {
            throw new EmailServiceException("MimeMessage cannot be null");
        }
        if (templateId == null) {
            throw new EmailServiceException("Template ID cannot be null");
        }
        if (employeeId == null) {
            throw new EmailServiceException("Employee ID cannot be null");
        }

        if (hasAttachments(attachmentTemplateIds, files)) {
            try {
                // Сохраняем старое тело письма
                String body = (String) message.getContent();
                String subject = message.getSubject();

                // Создаём multipart
                jakarta.mail.internet.MimeMultipart multipart = new jakarta.mail.internet.MimeMultipart();

                // Первая часть — тело письма
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body, "UTF-8");
                multipart.addBodyPart(textPart);

                // Вторая часть — вложение
                byte[] zipBytes = zipService.createZipArchive(
                        attachmentTemplateIds,
                        employeeId,
                        files
                );
                if (zipBytes == null || zipBytes.length == 0) {
                    throw new EmailServiceException("Failed to create zip archive - empty result");
                }
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(new jakarta.activation.DataHandler(
                        new ByteArrayDataSource(zipBytes, "application/zip")));
                attachmentPart.setFileName("приложения.zip");
                multipart.addBodyPart(attachmentPart);

                // Устанавливаем multipart как контент письма
                message.setContent(multipart);
                message.setSubject(subject);
            } catch (IOException e) {
                log.error("Failed to add attachments to email", e);
                throw new EmailServiceException("Failed to add attachments to email: " + e.getMessage(), e);
            }
        }
    }

    private boolean hasAttachments(List<Long> attachmentTemplateIds, List<MultipartFile> files) {
        if (attachmentTemplateIds == null) {
            throw new EmailServiceException("Attachment template IDs list cannot be null");
        }
        return !attachmentTemplateIds.isEmpty() || (files != null && !files.isEmpty());
    }
}