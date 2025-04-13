package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.dto.documents.EmailResponse;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.GmailApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailApiServiceImpl implements GmailApiService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public EmailResponse sendEmail(MimeMessage message) {
        try {
            // Get the authenticated user's email
            String userEmail = getUserEmail();
            if (userEmail == null) {
                return new EmailResponse(false, null, "User email not found");
            }
            
            // Load the OAuth2 authorized client
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", userEmail);
            if (client == null) {
                return new EmailResponse(false, null, "OAuth2 authorization not found. Please authenticate with Google.");
            }
            
            OAuth2AccessToken accessToken = client.getAccessToken();
            Gmail gmailService = initializeGmailService(accessToken.getTokenValue());
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            message.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
            
            Message gmailMessage = new Message();
            gmailMessage.setRaw(encodedEmail);
            
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
            // Get the authenticated user's email
            String userEmail = getUserEmail();
            if (userEmail == null) {
                throw new EmailServiceException("User email not found");
            }
            
            // Load the OAuth2 authorized client
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", userEmail);
            if (client == null) {
                throw new EmailServiceException("OAuth2 authorization not found. Please authenticate with Google.");
            }
            
            OAuth2AccessToken accessToken = client.getAccessToken();
            Gmail gmailService = initializeGmailService(accessToken.getTokenValue());
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            message.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
            
            Message gmailMessage = new Message();
            gmailMessage.setRaw(encodedEmail);
            
            Draft draft = new Draft();
            draft.setMessage(gmailMessage);
            
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
            // Decode the MIME message
            byte[] emailBytes = Base64.getUrlDecoder().decode(encodedEmail);
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
            
            // Update sender and recipient data
            message.setFrom(from);
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            
            // Encode the updated MIME message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            message.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            return Base64.getUrlEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("Failed to update MIME message", e);
            throw new EmailServiceException("Failed to update MIME message", e);
        }
    }
    
    private Gmail initializeGmailService(String accessToken) throws GeneralSecurityException, IOException {
        HttpRequestInitializer requestInitializer = request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        };
        
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Department Assistant")
                .build();
    }
    
    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            return user.getEmail();
        }
        return null;
    }
} 