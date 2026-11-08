package com.cloudmaint.incident.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
// Con esto realizaremos llamadas HTTP síncronas
import org.springframework.web.client.RestClient;

import com.cloudmaint.incident.dto.AssetResponse;

// Con esto ambos servicios siguen siendo independientes, solamente acceden a la API REST
@Component
public class AssetClient {
	
	// Para las peticiones
    private final RestClient restClient;
    
    // Usa variable detallada en application.properties
    public AssetClient(
            @Value("${asset-service.url}") String assetServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(assetServiceUrl)
                .build();
    }
    
    // Obtener un asset mediante un id (y petición HTTP)
    public AssetResponse getAssetById(Long assetId) {

        return restClient
                .get()
                .uri("/api/assets/{id}", assetId)
                .retrieve()
                .body(AssetResponse.class);
    }
}