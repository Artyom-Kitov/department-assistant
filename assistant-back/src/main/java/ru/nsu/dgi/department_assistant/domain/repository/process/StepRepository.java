package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepId;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface StepRepository extends JpaRepository<Step, StepId> {
    List<Step> findAllByProcessId(UUID processId);

}
