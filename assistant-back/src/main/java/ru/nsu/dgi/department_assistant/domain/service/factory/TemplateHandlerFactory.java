package ru.nsu.dgi.department_assistant.domain.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.handler.DocxTemplateHandler;
import ru.nsu.dgi.department_assistant.domain.service.handler.TemplateHandler;

@Service
public class TemplateHandlerFactory {
    private final ApplicationContext applicationContext;

    @Autowired
    public TemplateHandlerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public TemplateHandler getHandler(String templateType) {
        switch (templateType) {
            case "txt":
            case "html":
            case "docx":
                return applicationContext.getBean(DocxTemplateHandler.class);
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип шаблона: " + templateType);
        }
    }
}