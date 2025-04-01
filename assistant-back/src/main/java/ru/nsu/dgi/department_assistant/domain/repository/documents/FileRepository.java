package ru.nsu.dgi.department_assistant.domain.repository.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.documents.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}