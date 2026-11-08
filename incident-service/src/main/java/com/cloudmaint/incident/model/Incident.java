package com.cloudmaint.incident.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Nueva tabla en PostgreSQL con nombre incidents
// Mismas etiquetas que en Asset-service
@Entity
@Table(name = "incidents")
public class Incident {
	// GETTERS Y SETTERS GENERADOS POR ECLIPSE
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceImpact getServiceImpact() {
		return serviceImpact;
	}

	public void setServiceImpact(ServiceImpact serviceImpact) {
		this.serviceImpact = serviceImpact;
	}

	public SafetyImpact getSafetyImpact() {
		return safetyImpact;
	}

	public void setSafetyImpact(SafetyImpact safetyImpact) {
		this.safetyImpact = safetyImpact;
	}

	public Workaround getWorkaround() {
		return workaround;
	}

	public void setWorkaround(Workaround workaround) {
		this.workaround = workaround;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public IncidentStatus getStatus() {
		return status;
	}

	public void setStatus(IncidentStatus status) {
		this.status = status;
	}

	public Integer getSlaHours() {
		return slaHours;
	}

	public void setSlaHours(Integer slaHours) {
		this.slaHours = slaHours;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long assetId;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceImpact serviceImpact;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafetyImpact safetyImpact;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Workaround workaround;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status = IncidentStatus.OPEN;

    @Column(nullable = false)
    private Integer slaHours;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime deadline;

    public Incident() {
    }
}