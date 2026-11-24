package com.cloudmaint.asset.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.cloudmaint.asset.model.Asset;
import com.cloudmaint.asset.service.AssetService;
import com.cloudmaint.asset.dto.UpdateAssetRequest;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8082")
@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // RequestBody crea el activo con los datos del JSON | Valid obliga a que cumpla las validaciones impuestas
    // Petición POST para crear un activo
    @PostMapping
    public Asset createAsset(@Valid @RequestBody Asset asset) {
        return assetService.createAsset(asset);
    }
    // Petición GET para obtener todos los activos
    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    // Petición GET para obtener activo identificado por id 
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long id) {

        Asset asset = assetService.getAssetById(id);

        return ResponseEntity.ok(asset);
    }
    
    // Petición PUT (antes POST), para modificar un activo existente
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssetRequest request) {

        Asset updatedAsset = assetService.updateAsset(id, request);

        return ResponseEntity.ok(updatedAsset);
    }
}