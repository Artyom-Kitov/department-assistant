package ru.nsu.dgi.department_assistant.domain.service.handler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface TemplateHandler<T> {
    T handleTemplate(File templateFile, Map<String, String> data) throws IOException;
}
