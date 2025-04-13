package ru.nsu.dgi.department_assistant.domain.service;

import jakarta.mail.internet.MimeMessage;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;

public interface GmailApiService {
    /**
     * Sends an email using the Gmail API
     * @param message The MIME message to send
     * @return EmailResponse containing the message ID and status
     */
    EmailResponse sendEmail(MimeMessage message);

    /**
     * Creates a draft email using the Gmail API
     * @param message The MIME message to save as draft
     * @return The ID of the created draft
     */
    String createDraft(MimeMessage message);
} 