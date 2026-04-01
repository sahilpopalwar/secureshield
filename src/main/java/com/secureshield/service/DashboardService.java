package com.secureshield.service;

import com.secureshield.model.Alert;
import com.secureshield.model.Incident;
import com.secureshield.repository.AlertRepository;
import com.secureshield.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class DashboardService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private AlertRepository alertRepository;

    private final Random random = new Random();

    private final String[] types = {"SQL Injection", "XSS Reflected", "Path Traversal", "Brute Force", "Command Injection", "SSRF Attempt"};
    private final String[] ips = {"185.220.101.47", "45.155.205.33", "91.108.4.12", "194.165.16.61", "185.56.83.99", "198.51.100.23"};
    private final String[] severities = {"critical", "high", "medium"};

    @PostConstruct
    public void initData() {
        // Seed initial data
        for (int i = 0; i < 20; i++) {
            createMockIncident();
        }
        for (int i = 0; i < 14; i++) {
            createMockAlert();
        }
    }

    @Scheduled(fixedRate = 30000) // Every 30s
    public void generateIncidents() {
        if (random.nextInt(10) > 6) { // 30% chance
            createMockIncident();
        }
    }

    public List<Incident> getRecentIncidents() {
        return incidentRepository.findRecentIncidents();
    }

    public List<Alert> getRecentAlerts() {
        return alertRepository.findRecentAlerts();
    }

    public List<Incident> getIncidentsBySeverity(String severity) {
        return incidentRepository.findBySeverityOrderByCreatedAtDesc(severity);
    }

    private void createMockIncident() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Incident incident = new Incident(
            time,
            types[random.nextInt(types.length)],
            ips[random.nextInt(ips.length)],
            severities[random.nextInt(severities.length)],
            random.nextBoolean() ? "blocked" : "monitor"
        );
        incidentRepository.save(incident);
    }

    private void createMockAlert() {
        String ts = LocalDateTime.now().minusMinutes(random.nextInt(60)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Alert alert = new Alert(
            ts,
            "A0" + (random.nextInt(10) + 1),
            types[random.nextInt(types.length)] + " detected",
            ips[random.nextInt(ips.length)],
            "443",
            severities[random.nextInt(severities.length)],
            random.nextBoolean() ? "blocked" : "active"
        );
        alertRepository.save(alert);
    }
}
