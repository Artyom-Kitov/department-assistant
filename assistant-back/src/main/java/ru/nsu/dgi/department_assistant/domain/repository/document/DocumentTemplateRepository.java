package ru.nsu.dgi.department_assistant.domain.repository.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Integer> {
}
