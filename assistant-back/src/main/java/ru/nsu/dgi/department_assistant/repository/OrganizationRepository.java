package ru.nsu.dgi.department_assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
}

