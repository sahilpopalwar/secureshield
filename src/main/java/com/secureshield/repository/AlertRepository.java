package com.secureshield.repository;

import com.secureshield.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a ORDER BY a.createdAt DESC")
    List<Alert> findRecentAlerts();

    List<Alert> findBySeverityOrderByCreatedAtDesc(String severity);
}
