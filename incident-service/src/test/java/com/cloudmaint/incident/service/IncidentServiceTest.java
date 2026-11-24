package com.cloudmaint.incident.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import java.time.LocalDateTime;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudmaint.incident.model.SlaStatus;
import com.cloudmaint.incident.dto.UpdateIncidentStatusRequest;
import com.cloudmaint.incident.client.AssetClient;
import com.cloudmaint.incident.dto.AssetResponse;
import com.cloudmaint.incident.dto.CreateIncidentRequest;
import com.cloudmaint.incident.model.Criticality;
import com.cloudmaint.incident.model.Incident;
import com.cloudmaint.incident.model.IncidentStatus;
import com.cloudmaint.incident.model.Priority;
import com.cloudmaint.incident.model.SafetyImpact;
import com.cloudmaint.incident.model.ServiceImpact;
import com.cloudmaint.incident.model.Severity;
import com.cloudmaint.incident.model.Workaround;
import com.cloudmaint.incident.repository.IncidentRepository;

/*
 * Tests unitarios de IncidentService.
 *
 * Se simulan IncidentRepository y AssetClient para comprobar
 * la lógica de negocio sin utilizar PostgreSQL ni realizar
 * llamadas HTTP reales a asset-service.
 */
@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private AssetClient assetClient;

    private IncidentService incidentService;

    private CreateIncidentRequest request;

    private AssetResponse assetResponse;

    // Escenario básico antes de cada test
    @BeforeEach
    void setUp() {

        incidentService = new IncidentService(
                incidentRepository,
                assetClient
        );

        request = new CreateIncidentRequest();
        request.setAssetId(1L);
        request.setDescription("Fallo de bomba PCI");

        assetResponse = new AssetResponse();
        assetResponse.setId(1L);
        assetResponse.setName("Bomba PCI Nave A");
    }

    /*
     * Comprueba un caso crítico:
     *
     * Impacto total       -> 4 puntos
     * Seguridad inmediata -> 4 puntos
     * Sin alternativa     -> 2 puntos
     *
     * Total = 10 -> Severity.CRITICAL
     *
     * Si además el activo es CRITICAL:
     * Priority.CRITICAL -> SLA de 2 horas.
     */
    @Test
    void shouldCreateCriticalIncident() {

        request.setServiceImpact(ServiceImpact.TOTAL);
        request.setSafetyImpact(SafetyImpact.IMMEDIATE);
        request.setWorkaround(Workaround.NONE);

        assetResponse.setCriticality(Criticality.CRITICAL);

        when(assetClient.getAssetById(1L))
                .thenReturn(assetResponse);

        /*
         * Cuando se intente guardar cualquier Incident,
         * devuelve ese mismo objeto.
         */
        when(incidentRepository.save(
                org.mockito.ArgumentMatchers.any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident result = incidentService.createIncident(request);

        assertEquals(Severity.CRITICAL, result.getSeverity());
        assertEquals(Priority.CRITICAL, result.getPriority());
        assertEquals(2, result.getSlaHours());
        assertEquals(IncidentStatus.OPEN, result.getStatus());

        verify(assetClient).getAssetById(1L);
        verify(incidentRepository).save(
                org.mockito.ArgumentMatchers.any(Incident.class));
    }

    /*
     * Comprueba una incidencia de gravedad baja
     * sobre un activo de criticidad baja.
     */
    @Test
    void shouldCreateLowPriorityIncident() {

        request.setServiceImpact(ServiceImpact.NONE);
        request.setSafetyImpact(SafetyImpact.NONE);
        request.setWorkaround(Workaround.FULL);

        assetResponse.setCriticality(Criticality.LOW);

        when(assetClient.getAssetById(1L))
                .thenReturn(assetResponse);

        when(incidentRepository.save(
                org.mockito.ArgumentMatchers.any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident result = incidentService.createIncident(request);

        assertEquals(Severity.LOW, result.getSeverity());
        assertEquals(Priority.LOW, result.getPriority());
        assertEquals(72, result.getSlaHours());
        assertEquals(IncidentStatus.OPEN, result.getStatus());
    }

    /*
     * Comprueba un caso intermedio:
     *
     * TOTAL     -> 4
     * NONE      -> 0
     * PARTIAL   -> 1
     *
     * Total = 5 -> Severity.MEDIUM
     *
     * Con criticidad HIGH:
     * Priority.HIGH -> SLA de 8 horas.
     */
    @Test
    void shouldCalculateMediumSeverityAndHighPriority() {

        request.setServiceImpact(ServiceImpact.TOTAL);
        request.setSafetyImpact(SafetyImpact.NONE);
        request.setWorkaround(Workaround.PARTIAL);

        assetResponse.setCriticality(Criticality.HIGH);

        when(assetClient.getAssetById(1L))
                .thenReturn(assetResponse);
        
        when(incidentRepository.save(
                org.mockito.ArgumentMatchers.any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident result = incidentService.createIncident(request);

        assertEquals(Severity.MEDIUM, result.getSeverity());
        assertEquals(Priority.HIGH, result.getPriority());
        assertEquals(8, result.getSlaHours());
    }
    
    /*
     * Comprueba que una incidencia puede actualizar su estado a RESOLVED.
     *
     * Cuando la incidencia pasa a RESOLVED, el servicio debe:
     * - actualizar correctamente el estado;
     * - asignar automáticamente la fecha y hora de resolución;
     * - persistir los cambios mediante IncidentRepository.
     */
    @Test
    void shouldUpdateIncidentStatusToResolved() {

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        // La incidencia dispone de un SLA válido.
        incident.setDeadline(LocalDateTime.now().plusHours(1));
        
        UpdateIncidentStatusRequest request =
                new UpdateIncidentStatusRequest();

        request.setStatus(IncidentStatus.RESOLVED);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        Incident result = incidentService.updateStatus(1L, request);

        assertEquals(IncidentStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());

        verify(incidentRepository).findById(1L);
        verify(incidentRepository).save(incident);
    }
    
    /*
     * Comprueba que una incidencia resuelta antes de la fecha límite
     * se clasifica como cumplida dentro del SLA.
     */
    @Test
    void shouldMarkIncidentWithinSla() {

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        // La fecha límite está una hora en el futuro.
        incident.setDeadline(LocalDateTime.now().plusHours(1));

        UpdateIncidentStatusRequest request =
                new UpdateIncidentStatusRequest();

        request.setStatus(IncidentStatus.RESOLVED);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        Incident result = incidentService.updateStatus(1L, request);

        assertEquals(IncidentStatus.RESOLVED, result.getStatus());
        assertEquals(SlaStatus.WITHIN_SLA, result.getSlaStatus());
    }
    
    /*
     * Comprueba que una incidencia resuelta después de la fecha límite
     * se clasifica como incumplimiento del SLA.
     */
    @Test
    void shouldMarkIncidentAsSlaBreached() {

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        // La fecha límite ya venció hace una hora.
        incident.setDeadline(LocalDateTime.now().minusHours(1));

        UpdateIncidentStatusRequest request =
                new UpdateIncidentStatusRequest();

        request.setStatus(IncidentStatus.RESOLVED);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        Incident result = incidentService.updateStatus(1L, request);

        assertEquals(IncidentStatus.RESOLVED, result.getStatus());
        assertEquals(SlaStatus.BREACHED, result.getSlaStatus());
    }
}