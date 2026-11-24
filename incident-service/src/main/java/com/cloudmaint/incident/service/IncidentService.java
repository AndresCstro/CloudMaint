package com.cloudmaint.incident.service;

import org.springframework.stereotype.Service;

import com.cloudmaint.incident.dto.CreateIncidentRequest;
import com.cloudmaint.incident.model.Priority;
import com.cloudmaint.incident.model.Severity;
import com.cloudmaint.incident.repository.IncidentRepository;
import com.cloudmaint.incident.model.Criticality;
import java.time.LocalDateTime;

import com.cloudmaint.incident.client.AssetClient;
import com.cloudmaint.incident.dto.AssetResponse;
import com.cloudmaint.incident.model.Incident;
import com.cloudmaint.incident.model.IncidentStatus;
import com.cloudmaint.incident.model.SlaStatus;
import com.cloudmaint.incident.dto.UpdateIncidentStatusRequest;


import java.util.List;
import java.util.Optional;


@Service
public class IncidentService {

	private final IncidentRepository incidentRepository;
	private final AssetClient assetClient;

	public IncidentService(
	        IncidentRepository incidentRepository,
	        AssetClient assetClient) {

	    this.incidentRepository = incidentRepository;
	    this.assetClient = assetClient;
	}
	
	public Incident createIncident(CreateIncidentRequest request) {

	    // 1. Consultamos el activo
	    AssetResponse asset = assetClient.getAssetById(request.getAssetId());

	    // 2. Calculamos la severidad
	    Severity severity = calculateSeverity(request);

	    // 3. Obtenemos la criticidad del activo
	    Criticality criticality = asset.getCriticality();

	    // 4. Calculamos la prioridad
	    Priority priority = calculatePriority(severity, criticality);

	    // 5. Calculamos el SLA
	    int slaHours = calculateSlaHours(priority);

	    // 6. Fechas --> calculada con el SLA
	    LocalDateTime createdAt = LocalDateTime.now();
	    LocalDateTime deadline = createdAt.plusHours(slaHours);

	    // 7. Construimos la incidencia
	    Incident incident = new Incident();

	    incident.setAssetId(request.getAssetId());
	    incident.setDescription(request.getDescription());
	    incident.setServiceImpact(request.getServiceImpact());
	    incident.setSafetyImpact(request.getSafetyImpact());
	    incident.setWorkaround(request.getWorkaround());

	    incident.setSeverity(severity);
	    incident.setPriority(priority);
	    incident.setStatus(IncidentStatus.OPEN);

	    incident.setSlaHours(slaHours);
	    incident.setCreatedAt(createdAt);
	    incident.setDeadline(deadline);

	    // 8. Guardamos en PostgreSQL
	    return incidentRepository.save(incident);
	}
	
	public List<Incident> getAllIncidents() {
	    return incidentRepository.findAll();
	}

	public Optional<Incident> getIncidentById(Long id) {
	    return incidentRepository.findById(id);
	}
    
    // Función para calcular la severidad de un incidente: ServiceImpact + SafetyImpact + Workaround
    private Severity calculateSeverity(CreateIncidentRequest request) {

        int score = 0;

        score += switch (request.getServiceImpact()) {
            case NONE -> 0;
            case DEGRADED -> 2;
            case TOTAL -> 4;
        };

        score += switch (request.getSafetyImpact()) {
            case NONE -> 0;
            case POTENTIAL -> 2;
            case IMMEDIATE -> 4;
        };

        score += switch (request.getWorkaround()) {
            case FULL -> 0;
            case PARTIAL -> 1;
            case NONE -> 2;
        };

        if (score <= 2) {
            return Severity.LOW;
        }

        if (score <= 5) {
            return Severity.MEDIUM;
        }

        if (score <= 8) {
            return Severity.HIGH;
        }

        return Severity.CRITICAL;
    }
    
    // Implementa la matriz del Hito 1 
    private Priority calculatePriority(Severity severity, Criticality criticality) {

        return switch (criticality) {

            case LOW -> switch (severity) {
                case LOW -> Priority.LOW;
                case MEDIUM -> Priority.LOW;
                case HIGH -> Priority.MEDIUM;
                case CRITICAL -> Priority.HIGH;
            };

            case MEDIUM -> switch (severity) {
                case LOW -> Priority.LOW;
                case MEDIUM -> Priority.MEDIUM;
                case HIGH -> Priority.HIGH;
                case CRITICAL -> Priority.HIGH;
            };

            case HIGH -> switch (severity) {
                case LOW -> Priority.MEDIUM;
                case MEDIUM -> Priority.HIGH;
                case HIGH -> Priority.HIGH;
                case CRITICAL -> Priority.CRITICAL;
            };

            case CRITICAL -> switch (severity) {
                case LOW -> Priority.HIGH;
                case MEDIUM -> Priority.HIGH;
                case HIGH -> Priority.CRITICAL;
                case CRITICAL -> Priority.CRITICAL;
            };
        };
    }
    
    // Función para calcular el SLA 
    private int calculateSlaHours(Priority priority) {

        return switch (priority) {
            case LOW -> 72;
            case MEDIUM -> 24;
            case HIGH -> 8;
            case CRITICAL -> 2;
        };
    }
    
    // Función para actualizar el estado de una incidencia
    public Incident updateStatus(
            Long id,
            UpdateIncidentStatusRequest request) {
    	
    	// Busca el incidente asociado a 'id'
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        
        // Se modifica el estado de la incidencia (a partir del DTO)
        incident.setStatus(request.getStatus());
        
        // Gestionar resolvedAt si es que se resuelve la incidencia
        if (request.getStatus() == IncidentStatus.RESOLVED) {
            LocalDateTime resolvedAt = LocalDateTime.now();

            incident.setResolvedAt(resolvedAt);

            SlaStatus slaStatus =
                    calculateSlaStatus(resolvedAt, incident.getDeadline());

            incident.setSlaStatus(slaStatus);
        }

        return incidentRepository.save(incident);
    }
    
    // Función para calcular el estado de SLA 
    private SlaStatus calculateSlaStatus(
            LocalDateTime resolvedAt,
            LocalDateTime deadline) {

        if (!resolvedAt.isAfter(deadline)) {
            return SlaStatus.WITHIN_SLA;
        }

        return SlaStatus.BREACHED;
    }
}