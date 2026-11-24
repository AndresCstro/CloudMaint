package com.cloudmaint.incident.dto;

import com.cloudmaint.incident.model.IncidentStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateIncidentStatusRequest {

    @NotNull
    private IncidentStatus status;

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }
}