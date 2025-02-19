package ru.nsu.dgi.department_assistant.domain.entity.document;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class EmailTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String subject;  // Тема письма

    @Column(nullable = false, length = 10000)
    private String body;  // Тело письма (может содержать переменные, например {{username}})

    @Column(nullable = false)
    private boolean isHtml;  // Использовать HTML-версию или просто текст?
}
