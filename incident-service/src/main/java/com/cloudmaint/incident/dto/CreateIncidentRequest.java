package com.cloudmaint.incident.dto;

import com.cloudmaint.incident.model.SafetyImpact;
import com.cloudmaint.incident.model.ServiceImpact;
import com.cloudmaint.incident.model.Workaround;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para impedir que el usuario decida atributos como: severity, priority, status...
// Esos atributos debe calcularlos la app
public class CreateIncidentRequest {

    @NotNull
    private Long assetId;

    @NotBlank
    private String description;

    @NotNull
    private ServiceImpact serviceImpact;

    @NotNull
    private SafetyImpact safetyImpact;

    @NotNull
    private Workaround workaround;

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
}