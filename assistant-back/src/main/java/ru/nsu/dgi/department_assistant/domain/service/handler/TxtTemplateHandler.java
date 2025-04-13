package ru.nsu.dgi.department_assistant.domain.service.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.exception.EmailServiceException;
import ru.nsu.dgi.department_assistant.domain.service.TemplateProcessingService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TxtTemplateHandler implements TemplateHandler<String> {
    
    private final TemplateProcessingService templateProcessingService;
    
    @Override
    public String handleTemplate(InputStream inputStream, Map<String, String> data) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        try {
            // Read template content
            byte[] bytes = inputStream.readAllBytes();
            String templateContent = new String(bytes, StandardCharsets.UTF_8);
            
            // Log the content length to verify we're reading the entire file
            log.debug("Read template content with length: {}", templateContent.length());
            
            // Use the existing template processing service to replace variables
            String processedContent = templateProcessingService.replaceWithCases(templateContent, data);
            
            // Log the processed content length
            log.debug("Processed template content with length: {}", processedContent.length());
            
            return processedContent;
        } catch (IOException e) {
            log.error("Failed to process text template", e);
            throw new EmailServiceException("Failed to process text template", e);
        } finally {
            // Ensure the input stream is closed
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close input stream", e);
            }
        }
    }
}
