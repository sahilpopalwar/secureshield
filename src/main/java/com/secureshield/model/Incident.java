package com.secureshield.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String time;
    private String type;
    private String ip;
    private String severity;
    private String action;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Incident() {}

    public Incident(String time, String type, String ip, String severity, String action) {
        this.time = time;
        this.type = type;
        this.ip = ip;
        this.severity = severity;
        this.action = action;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

