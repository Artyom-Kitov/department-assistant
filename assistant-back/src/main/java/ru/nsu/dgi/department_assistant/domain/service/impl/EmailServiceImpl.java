package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailRequest;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.ContactsService;
import ru.nsu.dgi.department_assistant.domain.service.EmailService;
import ru.nsu.dgi.department_assistant.domain.service.GmailApiService;
import ru.nsu.dgi.department_assistant.domain.service.TemplateHandlerDispatcherService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final TemplateHandlerDispatcherService templateHandlerDispatcherService;
    private final GmailApiService gmailApiService;
    private final ContactsService contactsService;

    @Override
    public EmailResponse sendEmail(EmailRequest request) {
        try {
            MimeMessage message = createMimeMessage(request);
            return gmailApiService.sendEmail(message);
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new EmailServiceException("Failed to send email", e);
        }
    }
    
    @Override
    public List<EmailResponse> sendBulk(List<EmailRequest> requests) {
        return requests.stream()
                .map(this::sendEmail)
                .toList();
    }
    
    @Override
    public String createDraft(EmailRequest request) {
        try {
            MimeMessage message = createMimeMessage(request);
            return gmailApiService.createDraft(message);
        } catch (Exception e) {
            log.error("Failed to create draft", e);
            throw new EmailServiceException("Failed to create draft", e);
        }
    }

    @Override
    public MimeMessage createMimeMessage(EmailRequest request) {
        try {
            String userEmail;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
                userEmail = user.getEmail();
            } else {
                // Fallback to the name if email is not available
                userEmail = authentication != null ? authentication.getName() : "unknown@example.com";
            }
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            
            message.setFrom(userEmail);
            
            // Get recipient email from contacts
            String recipientEmail = contactsService.getContactsByEmployeeId(request.employeeId()).email();
            message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
            
            // Process main template for email body using TemplateHandlerDispatcherService
            String processedBody = templateHandlerDispatcherService.processTemplate(
                request.templateId(),
                request.employeeId()
            );
            message.setText(processedBody);
            
            // Handle attachments
            if (!request.attachmentTemplateIds().isEmpty() || !request.uploadedFiles().isEmpty()) {
                byte[] zipBytes = createZipArchive(request);
                message.setContent(new ByteArrayDataSource(
                    zipBytes,
                    "application/zip"
                ), "attachments.zip");
            }
            
            return message;
        } catch (MessagingException e) {
            log.error("Failed to create MIME message", e);
            throw new EmailServiceException("Failed to create MIME message", e);
        }
    }

    private byte[] createZipArchive(EmailRequest request) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            // Process attachment templates
            for (Long templateId : request.attachmentTemplateIds()) {
                String processedContent = templateHandlerDispatcherService.processTemplate(
                    templateId,
                    request.employeeId()
                );
                
                ZipEntry entry = new ZipEntry(templateId + ".txt");
                zos.putNextEntry(entry);
                zos.write(processedContent.getBytes());
                zos.closeEntry();
            }
            
            // Add uploaded files
            for (MultipartFile file : request.uploadedFiles()) {
                ZipEntry entry = new ZipEntry(file.getOriginalFilename());
                zos.putNextEntry(entry);
                zos.write(file.getBytes());
                zos.closeEntry();
            }
            
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to create zip archive", e);
            throw new EmailServiceException("Failed to create zip archive", e);
        }
    }
}