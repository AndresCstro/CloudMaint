package com.cloudmaint.asset.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudmaint.asset.model.Asset;
import com.cloudmaint.asset.model.AssetStatus;
import com.cloudmaint.asset.model.AssetType;
import com.cloudmaint.asset.model.Criticality;
import com.cloudmaint.asset.repository.AssetRepository;


/*
 * Tests unitarios del servicio de gestión de activos.
 *
 * Se utiliza Mockito para simular AssetRepository y poder probar
 * AssetService de forma aislada, sin utilizar una base de datos real.
 */
@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

	// Repo simulado
    @Mock
    private AssetRepository assetRepository;

    private AssetService assetService;

    private Asset asset;

    // Antes de cada @Test se crea escenario de prueba
    // Inicializa assetService con el repo simulado y crea un activo
    @BeforeEach
    void setUp() {

        assetService = new AssetService(assetRepository);

        asset = new Asset(
                "Bomba PCI Nave A",
                AssetType.FIRE_PROTECTION,
                "Nave A",
                Criticality.CRITICAL
        );

        asset.setId(1L);
        asset.setStatus(AssetStatus.ACTIVE);
    }

    // Verificar que se crea un activo correctamente
    @Test
    void shouldCreateAsset() {

    	// Comportamiento del repo simulado
        when(assetRepository.save(asset)).thenReturn(asset);

        Asset result = assetService.createAsset(asset);

        // Verificar datos
        assertEquals(1L, result.getId());
        assertEquals("Bomba PCI Nave A", result.getName());
        assertEquals(Criticality.CRITICAL, result.getCriticality());

        // Verifica la llamada a save()
        verify(assetRepository).save(asset);
    }

    // Comprueba que el servicio devuelva la lista de activos
    @Test
    void shouldGetAllAssets() {
    	
    	// Comportamiento del repo simulado
        when(assetRepository.findAll()).thenReturn(List.of(asset));

        List<Asset> result = assetService.getAllAssets();
        
        // Comprobar lista y llamada al método findall()
        assertEquals(1, result.size());
        assertEquals("Bomba PCI Nave A", result.get(0).getName());

        verify(assetRepository).findAll();
    }

    // Comprobar identificación de un activo usando su identificador
    @Test
    void shouldGetAssetById() {

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

        Optional<Asset> result = assetService.getAssetById(1L);
        
        // Comprobar que el activo existe y que su ID es correcto
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        
        // Verificar que la búsqueda se haya delegado al repo
        verify(assetRepository).findById(1L);
    }
}