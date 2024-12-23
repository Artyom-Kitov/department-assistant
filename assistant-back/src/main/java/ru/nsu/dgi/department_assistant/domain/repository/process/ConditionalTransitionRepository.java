package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.ConditionalTransition;

import java.util.UUID;

@Repository
public interface ConditionalTransitionRepository extends JpaRepository<ConditionalTransition, UUID> {
}
