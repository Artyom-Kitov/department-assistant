package ru.nsu.dgi.department_assistant.domain.service.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface TemplateHandler<T> {
    T handleTemplate(InputStream inputStream, Map<String, String> data) throws IOException;
}
