package com.secureshield.controller;

import com.secureshield.model.Alert;
import com.secureshield.model.Incident;
import com.secureshield.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "https://localhost:8444") // Frontend only
public class DashboardApiController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getRecentIncidents() {
        return ResponseEntity.ok(dashboardService.getRecentIncidents());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getRecentAlerts() {
        return ResponseEntity.ok(dashboardService.getRecentAlerts());
    }

    @GetMapping("/incidents/{severity}")
    public ResponseEntity<List<Incident>> getIncidentsBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(dashboardService.getIncidentsBySeverity(severity));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        // Simple mock metrics (extend as needed)
        Map<String, Object> metrics = Map.of(
            "blockedThreats", 1247,
            "activeAlerts", dashboardService.getRecentAlerts().size(),
            "rps", 3892,
            "rulesActive", 482
        );
        return ResponseEntity.ok(metrics);
    }
}
