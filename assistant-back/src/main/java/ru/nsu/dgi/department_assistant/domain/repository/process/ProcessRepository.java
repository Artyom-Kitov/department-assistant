package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;

import java.util.UUID;

public interface ProcessRepository extends JpaRepository<Process, UUID> {
}
