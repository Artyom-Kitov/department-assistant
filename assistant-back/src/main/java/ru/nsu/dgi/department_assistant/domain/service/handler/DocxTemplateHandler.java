package ru.nsu.dgi.department_assistant.domain.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidDocumentException;
import ru.nsu.dgi.department_assistant.domain.service.impl.TemplateProcessingServiceImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
        } catch (IOException e) {
            throw new InvalidDocumentException("Failed to read document from input stream", e);
        }

        try {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs == null || runs.isEmpty()) {
                    continue;
                }

                StringBuilder paragraphText = new StringBuilder();
                for (XWPFRun run : runs) {
                    String runText = run.getText(0);
                    if (runText != null) {
                        paragraphText.append(runText);
                    }
                }
                String fullParagraphText = paragraphText.toString();

                if (fullParagraphText.contains("{{")) {
                    String processedText = templateProcessingService.replaceWithCases(fullParagraphText, data);

                    for (int i = runs.size() - 1; i > 0; i--) {
                        paragraph.removeRun(i);
                    }
                    XWPFRun newRun = runs.get(0);
                    newRun.setText(processedText, 0);
                }
            }
        } catch (Exception e) {
            document.close();
            throw new InvalidDocumentException("Failed to process document", e);
        }

        return document;
    }
}

