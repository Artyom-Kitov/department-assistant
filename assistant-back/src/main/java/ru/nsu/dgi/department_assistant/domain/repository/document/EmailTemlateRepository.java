package ru.nsu.dgi.department_assistant.domain.repository.document;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.document.EmailTemplate;

import java.util.Optional;

public interface EmailTemlateRepository extends JpaRepository<EmailTemplate, Integer> {
    Optional<EmailTemplate> findByTemplateKey(String templateKey);
}
