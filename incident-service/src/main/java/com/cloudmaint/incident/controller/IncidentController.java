package com.cloudmaint.incident.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cloudmaint.incident.dto.CreateIncidentRequest;
import com.cloudmaint.incident.model.Incident;
import com.cloudmaint.incident.service.IncidentService;

import java.util.List;



import jakarta.validation.Valid;

// Manejo de peticiones HTTP de incidencias
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(
            @Valid @RequestBody CreateIncidentRequest request) {

        Incident incident = incidentService.createIncident(request);
        
        // Devuelve 201 CREATED en lugar de 200 OK --> futura modificación de asset
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(incident);
    }
    
    // Petición GET para obtener todas las incidencias
    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }
    
    // Petición GET para obtener una incidencia especificada por id
    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {

        return incidentService.getIncidentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}