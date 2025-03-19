package ru.nsu.dgi.department_assistant.domain.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.TemplateProcessingService;
import ru.nsu.dgi.department_assistant.domain.service.impl.TemplateProcessingServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocxTemplateHandler implements TemplateHandler<XWPFDocument> {
    private final TemplateProcessingServiceImpl templateProcessingService;
    @Override
    public XWPFDocument handleTemplate(File file, Map<String, String> data) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            XWPFDocument document = new XWPFDocument(inputStream);
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        text = templateProcessingService.replaceWithCases(text, data);
                        run.setText(text, 0);
                    }
                }
            }
            return document;
        }
    }
}
