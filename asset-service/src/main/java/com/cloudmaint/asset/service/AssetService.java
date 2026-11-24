package com.cloudmaint.asset.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudmaint.asset.dto.UpdateAssetRequest;
import com.cloudmaint.asset.model.Asset;
import com.cloudmaint.asset.repository.AssetRepository;
import com.cloudmaint.asset.exception.AssetNotFoundException;
@Service
public class AssetService {

	// El repo JPA
    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    
    // Aquí ya se hace uso de las funciones de JPA
    // Crear activo nuevo
    public Asset createAsset(Asset asset) {
        return assetRepository.save(asset);
    }
    
    // Listar todos los activos
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
    
    // Obtener un activo mediante su id --> emplea la excepción específica
    public Asset getAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException(id));
    }
    
    
    // Dirty checking: Transactional permite que Hibernate detecte el cambio en el asset y haga un UPDATE automático
    // Actualizar activo existente (mejora posterior: emplear un DTO)
    @Transactional
    public Asset updateAsset(Long id, UpdateAssetRequest request) {

        Asset existingAsset = assetRepository.findById(id)
        		.orElseThrow(() -> new AssetNotFoundException(id));
        
        existingAsset.setName(request.getName());
        existingAsset.setType(request.getType());
        existingAsset.setLocation(request.getLocation());
        existingAsset.setCriticality(request.getCriticality());

        return assetRepository.save(existingAsset);
    }
}