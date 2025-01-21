package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;

import java.util.List;
import java.util.UUID;

@Repository
public interface StepRepository extends JpaRepository<Step, UUID> {
    List<Step> findAllByProcess(Process process);
}
