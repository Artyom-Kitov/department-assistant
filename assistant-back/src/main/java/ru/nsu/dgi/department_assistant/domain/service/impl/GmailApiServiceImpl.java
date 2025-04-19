package ru.nsu.dgi.department_assistant.domain.service.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.GmailApiService;
import ru.nsu.dgi.department_assistant.domain.service.SecurityService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailApiServiceImpl implements GmailApiService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final SecurityService securityService;

    @Override
    public EmailResponse sendEmail(MimeMessage message) {
        try {
            String userEmail = securityService.getCurrentUserEmail();
            OAuth2AuthorizedClient client = getAuthorizedClient(userEmail);
            Gmail gmailService = initializeGmailService(client.getAccessToken().getTokenValue());
            
            Message gmailMessage = createGmailMessage(message);
            Message sentMessage = gmailService.users().messages().send("me", gmailMessage).execute();
            
            log.info("Email sent successfully with ID: {}", sentMessage.getId());
            return new EmailResponse(true, sentMessage.getId(), null);
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return new EmailResponse(false, null, e.getMessage());
        }
    }

    @Override
    public String createDraft(MimeMessage message) {
        try {
            String userEmail = securityService.getCurrentUserEmail();
            OAuth2AuthorizedClient client = getAuthorizedClient(userEmail);
            Gmail gmailService = initializeGmailService(client.getAccessToken().getTokenValue());
            
            Message gmailMessage = createGmailMessage(message);
            Draft draft = new Draft().setMessage(gmailMessage);
            Draft createdDraft = gmailService.users().drafts().create("me", draft).execute();
            
            log.info("Draft created successfully with ID: {}", createdDraft.getId());
            return createdDraft.getId();
        } catch (Exception e) {
            log.error("Failed to create draft", e);
            throw new EmailServiceException("Failed to create draft", e);
        }
    }


    public String updateMimeMessage(String encodedEmail, String from, String to, String subject) {
        try {
            MimeMessage message = decodeMimeMessage(encodedEmail);
            updateMessageHeaders(message, from, to, subject);
            return encodeMimeMessage(message);
        } catch (Exception e) {
            log.error("Failed to update MIME message", e);
            throw new EmailServiceException("Failed to update MIME message", e);
        }
    }
    
    private OAuth2AuthorizedClient getAuthorizedClient(String userEmail) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", userEmail);
        if (client == null) {
            throw new EmailServiceException("OAuth2 authorization not found. Please authenticate with Google.");
        }
        return client;
    }
    
    private Gmail initializeGmailService(String accessToken) throws GeneralSecurityException, IOException {
        HttpRequestInitializer requestInitializer = request -> request.getHeaders().setAuthorization("Bearer " + accessToken);
        
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Department Assistant")
                .build();
    }

    private Message createGmailMessage(MimeMessage message) throws IOException, MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        message.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        
        Message gmailMessage = new Message();
        gmailMessage.setRaw(encodedEmail);
        return gmailMessage;
    }

    private MimeMessage decodeMimeMessage(String encodedEmail) throws Exception {
        byte[] emailBytes = Base64.getUrlDecoder().decode(encodedEmail);
        Session session = Session.getDefaultInstance(new Properties(), null);
        return new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    }

    private void updateMessageHeaders(MimeMessage message, String from, String to, String subject) throws Exception {
        message.setFrom(from);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
    }

    private String encodeMimeMessage(MimeMessage message) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        message.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
} 