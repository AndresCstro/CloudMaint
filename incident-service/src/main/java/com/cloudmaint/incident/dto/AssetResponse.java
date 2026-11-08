package com.cloudmaint.incident.dto;

import com.cloudmaint.incident.model.Criticality;

// Respuesta de Asset-Service (realmente solo necesitamos saber la criticalidad)
public class AssetResponse {

    private Long id;
    private String name;
    private Criticality criticality;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Criticality getCriticality() {
        return criticality;
    }

    public void setCriticality(Criticality criticality) {
        this.criticality = criticality;
    }
}