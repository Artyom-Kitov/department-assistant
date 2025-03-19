package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.DeclensionService;
import ru.nsu.dgi.department_assistant.domain.service.TemplateProcessingService;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TemplateProcessingServiceImpl implements TemplateProcessingService {

    private final DeclensionService declensionService;

    public String replaceWithCases(String text, Map<String, String> data) {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)/(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String key = matcher.group(1);
            String caseType = matcher.group(2);
            String value = data.get(key);

            if (value != null) {
                String declinedValue = declensionService.declineName(key, value, caseType);
                text = text.replace(matcher.group(0), declinedValue);
            }
        }

        return text;
    }
}