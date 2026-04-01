package com.secureshield.repository;

import com.secureshield.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("SELECT i FROM Incident i ORDER BY i.createdAt DESC LIMIT 10")
    List<Incident> findRecentIncidents();

    List<Incident> findBySeverityOrderByCreatedAtDesc(String severity);
}
