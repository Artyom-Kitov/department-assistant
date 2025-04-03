package ru.nsu.dgi.department_assistant.domain.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidDocumentException;
import ru.nsu.dgi.department_assistant.domain.service.TemplateProcessingService;
import ru.nsu.dgi.department_assistant.domain.service.impl.TemplateProcessingServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class DocxTemplateHandler implements TemplateHandler<XWPFDocument> {
    private final TemplateProcessingServiceImpl templateProcessingService;
    @Override
    public XWPFDocument handleTemplate(InputStream inputStream, Map<String, String> data) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        XWPFDocument document;
        try {
            document = new XWPFDocument(inputStream);
        } catch (Exception e) {
            throw new InvalidDocumentException("Failed to read document from input stream", e);
        }

        try {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        try {
                            text = templateProcessingService.replaceWithCases(text, data);
                            run.setText(text, 0);
                        } catch (Exception e) {
                            log.error("Failed to replace text in document: {}", e.getMessage());
                            throw new InvalidDocumentException("Failed to replace text in document", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Закрываем документ в случае ошибки
            document.close();
            throw new InvalidDocumentException("Failed to process document", e);
        }

        return document;
    }
}

