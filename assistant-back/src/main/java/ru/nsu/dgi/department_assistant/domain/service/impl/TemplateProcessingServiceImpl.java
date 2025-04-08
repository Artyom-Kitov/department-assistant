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

    @Override
    public String replaceWithCases(String text, Map<String, String> data) {

        Pattern pattern = Pattern.compile("\\{\\{(?<key>[a-zA-Z0-9.]+)(?:/(?<case>[a-zA-Zа-яА-Я0-9]+))?}}");
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group("key");
            String caseType = matcher.group("case");
            String value = data.get(key);

            if (value != null) {
                String replacement = (caseType != null)
                        ? declensionService.declineName(key, value, caseType)
                        : value;
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}