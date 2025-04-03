package ru.nsu.dgi.department_assistant.domain.entity.documents;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "templates", name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fs_file_name")
    private String fsFileName;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_extension", nullable = false)
    private String fileExtension;

    @Column(name = "template_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    @Column(name = "size")
    private Long size;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "subject_text")
    private String subjectText;

    public enum TemplateType {
        DOCUMENT,
        EMAIL
    }
}
