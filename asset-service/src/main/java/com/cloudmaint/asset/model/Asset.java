package com.cloudmaint.asset.model;

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

// Al no emplear lombok debo declarar Getter y Setters --> Para dejar de manera explícita que hace cada parte
// Entidad almacenada en la base de datos --> con el nombre de assets
@Entity
@Table(name = "assets")
public class Asset {
	
	// Los id's los genera Postgre automáticamente 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // NotBlank evita campos en blanco
    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;

    @NotBlank
    @Column(nullable = false)
    private String location;

    // Lo guardo con String en lugar de entero por si en algún momento cambio orden enum
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Criticality criticality;
    
    // Al crear activo se le asigna de manera automática el estado de activo
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status = AssetStatus.ACTIVE;

    public Asset() {
    }

    public Asset(
            String name,
            AssetType type,
            String location,
            Criticality criticality) {

        this.name = name;
        this.type = type;
        this.location = location;
        this.criticality = criticality;
        this.status = AssetStatus.ACTIVE;
    }
    // GETTERS Y SETTERS
    
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

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }
}