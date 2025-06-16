package io.farmx.controller;

import io.farmx.dto.FarmCropsDTO;
import io.farmx.dto.PlantedCropDTO;
import io.farmx.service.PlantedCropService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; 
import java.util.List;

@RestController
@RequestMapping("/planted-crops")
public class PlantedCropController {

    private final PlantedCropService plantedCropService;

    public PlantedCropController(PlantedCropService plantedCropService) {
        this.plantedCropService = plantedCropService;
    }
    @PreAuthorize("hasRole('FARMER')")
    @PostMapping
    public ResponseEntity<PlantedCropDTO> addPlantedCrop(@RequestBody PlantedCropDTO dto, Principal principal) {
        PlantedCropDTO saved = plantedCropService.addPlantedCrop(dto, principal);
        return ResponseEntity.ok(saved);
    }
    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<PlantedCropDTO>> getCropsByFarm(@PathVariable Long farmId, Principal principal) {
        List<PlantedCropDTO> list = plantedCropService.getPlantedCropsForFarm(farmId, principal);
        return ResponseEntity.ok(list);
    }
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @PutMapping("/{id}")
    public ResponseEntity<PlantedCropDTO> updatePlantedCrop(@PathVariable Long id,
                                                            @RequestBody PlantedCropDTO dto,
                                                            Principal principal) {
        PlantedCropDTO updated = plantedCropService.updatePlantedCrop(id, dto, principal);
        return ResponseEntity.ok(updated);
    }
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlantedCrop(@PathVariable Long id, Principal principal) {
        plantedCropService.deletePlantedCrop(id, principal);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/by-farmer")
        public ResponseEntity<List<FarmCropsDTO>> getCropsByFarmer(Principal principal) {
            List<FarmCropsDTO> result = plantedCropService.getAllCropsByFarmer(principal);
            return ResponseEntity.ok(result);
        }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlantedCropDTO>> getAllCropsForAdmin() {
        List<PlantedCropDTO> result = plantedCropService.getAllPlantedCrops();
        return ResponseEntity.ok(result);
    }



  
}
