package com.cloudmaint.incident.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudmaint.incident.model.Incident;

// De nuevo empleo JPARepo para contar con las funciones que ofrece
public interface IncidentRepository extends JpaRepository<Incident, Long> {
}