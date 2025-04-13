package ru.nsu.dgi.department_assistant.domain.service;

import java.util.List;

import jakarta.mail.internet.MimeMessage;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailRequest;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;

public interface EmailService {
    /**
     * Creates a MIME message from the email request
     * @param request The email request containing template and data
     * @return The created MIME message
     */
    MimeMessage createMimeMessage(EmailRequest request);

    /**
     * Sends an email using the Gmail API
     * @param request The email request containing template and data
     * @return EmailResponse containing the message ID and status
     */
    EmailResponse sendEmail(EmailRequest request);

    /**
     * Sends multiple emails in bulk
     * @param requests List of email requests
     * @return List of email responses
     */
    List<EmailResponse> sendBulk(List<EmailRequest> requests);

    /**
     * Creates a draft email
     * @param request The email request containing template and data
     * @return The ID of the created draft
     */
    String createDraft(EmailRequest request);
}
