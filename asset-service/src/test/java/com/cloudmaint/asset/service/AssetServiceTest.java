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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cloudmaint.asset.exception.AssetNotFoundException;
import com.cloudmaint.asset.dto.UpdateAssetRequest;
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

        Asset result =
                assetService.getAssetById(1L);

        assertEquals(asset, result);

        verify(assetRepository).findById(1L);
    }
    
    // Comprobar que se lanza la excepción creada
    @Test
    void shouldThrowExceptionWhenAssetDoesNotExist() {

        when(assetRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                AssetNotFoundException.class,
                () -> assetService.getAssetById(999L)
        );

        verify(assetRepository).findById(999L);
    }
    
    // Comprobar que el DTO modifica los atributos excepto el status y el ID
    @Test
    void shouldUpdateAsset() {

        Asset existingAsset = new Asset();
        existingAsset.setId(1L);
        existingAsset.setName("Cuadro antiguo");
        existingAsset.setType(AssetType.ELECTRICAL);
        existingAsset.setLocation("Sala antigua");
        existingAsset.setCriticality(Criticality.MEDIUM);
        existingAsset.setStatus(AssetStatus.ACTIVE);

        UpdateAssetRequest request = new UpdateAssetRequest();
        request.setName("Cuadro General BT");
        request.setType(AssetType.ELECTRICAL);
        request.setLocation("Sala eléctrica principal");
        request.setCriticality(Criticality.HIGH);

        when(assetRepository.findById(1L))
                .thenReturn(Optional.of(existingAsset));

        when(assetRepository.save(existingAsset))
                .thenReturn(existingAsset);

        Asset result =
                assetService.updateAsset(1L, request);

        assertEquals("Cuadro General BT", result.getName());
        assertEquals(AssetType.ELECTRICAL, result.getType());
        assertEquals("Sala eléctrica principal", result.getLocation());
        assertEquals(Criticality.HIGH, result.getCriticality());

        // El PUT general no debe modificar el estado.
        assertEquals(AssetStatus.ACTIVE, result.getStatus());

        verify(assetRepository).findById(1L);
        verify(assetRepository).save(existingAsset);
    }
    
}