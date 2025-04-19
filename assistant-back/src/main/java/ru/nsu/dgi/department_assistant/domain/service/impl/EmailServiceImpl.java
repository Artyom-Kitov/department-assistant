package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailRequest;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final TemplateHandlerDispatcherService templateHandlerDispatcherService;
    private final GmailApiService gmailApiService;
    private final ContactsService contactsService;
    private final SecurityService securityService;
    private final ZipServiceImpl zipService;

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
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            
            setMessageHeaders(message, request);
            setMessageBody(message, request);
            addAttachmentsIfNeeded(message, request);
            
            return message;
        } catch (MessagingException | IOException e) {
            log.error("Failed to create MIME message", e);
            throw new EmailServiceException("Failed to create MIME message", e);
        }
    }

    private void setMessageHeaders(MimeMessage message, EmailRequest request) throws MessagingException {
        String userEmail = securityService.getCurrentUserEmail();
        String recipientEmail = contactsService.getContactsByEmployeeId(request.employeeId()).email();
        
        message.setFrom(userEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
    }

    private void setMessageBody(MimeMessage message, EmailRequest request) throws MessagingException {
        String processedBody = templateHandlerDispatcherService.processTemplate(
            request.templateId(),
            request.employeeId()
        );
        message.setText(processedBody);
    }

    private void addAttachmentsIfNeeded(MimeMessage message, EmailRequest request) throws MessagingException, IOException {
        if (hasAttachments(request)) {
            byte[] zipBytes = zipService.createZipArchive(
                request.attachmentTemplateIds(),
                request.employeeId(),
                request.uploadedFiles()
            );
            message.setContent(new ByteArrayDataSource(
                zipBytes,
                "application/zip"
            ), "attachments.zip");
        }
    }

    private boolean hasAttachments(EmailRequest request) {
        return !request.attachmentTemplateIds().isEmpty() || !request.uploadedFiles().isEmpty();
    }
}