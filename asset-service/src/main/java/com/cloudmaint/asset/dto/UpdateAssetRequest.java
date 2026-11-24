package com.cloudmaint.asset.dto;

import com.cloudmaint.asset.model.AssetType;
import com.cloudmaint.asset.model.Criticality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para evitar que el usuario mande un JSON con status o el id 
public class UpdateAssetRequest {

    @NotBlank
    private String name;

    @NotNull
    private AssetType type;

    @NotBlank
    private String location;

    @NotNull
    private Criticality criticality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Criticality getCriticality() {
        return criticality;
    }

    public void setCriticality(Criticality criticality) {
        this.criticality = criticality;
    }
}