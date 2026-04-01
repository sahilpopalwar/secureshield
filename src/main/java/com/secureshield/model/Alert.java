package com.secureshield.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String timestamp;

    @NotNull
    private String owasp;

    @NotNull
    private String description;

    @NotNull
    private String ip;

    private String port;

    @NotNull
    private String severity;

    @NotNull
    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Alert() {}

    public Alert(String timestamp, String owasp, String description, String ip, String port, String severity, String status) {
        this.timestamp = timestamp;
        this.owasp = owasp;
        this.description = description;
        this.ip = ip;
        this.port = port;
        this.severity = severity;
        this.status = status;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getOwasp() { return owasp; }
    public void setOwasp(String owasp) { this.owasp = owasp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
