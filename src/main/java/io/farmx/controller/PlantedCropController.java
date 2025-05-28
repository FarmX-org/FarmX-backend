package io.farmx.controller;

import io.farmx.dto.FarmCropsDTO;
import io.farmx.dto.PlantedCropDTO;
import io.farmx.service.PlantedCropService;

import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<PlantedCropDTO> addPlantedCrop(@RequestBody PlantedCropDTO dto, Principal principal) {
        PlantedCropDTO saved = plantedCropService.addPlantedCrop(dto, principal);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<PlantedCropDTO>> getCropsByFarm(@PathVariable Long farmId, Principal principal) {
        List<PlantedCropDTO> list = plantedCropService.getPlantedCropsForFarm(farmId, principal);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantedCropDTO> updatePlantedCrop(@PathVariable Long id,
                                                            @RequestBody PlantedCropDTO dto,
                                                            Principal principal) {
        PlantedCropDTO updated = plantedCropService.updatePlantedCrop(id, dto, principal);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlantedCrop(@PathVariable Long id, Principal principal) {
        plantedCropService.deletePlantedCrop(id, principal);
        return ResponseEntity.noContent().build();
    }

        @GetMapping("/by-farmer")
        public ResponseEntity<List<FarmCropsDTO>> getCropsByFarmer(Principal principal) {
            List<FarmCropsDTO> result = plantedCropService.getAllCropsByFarmer(principal);
            return ResponseEntity.ok(result);
        }


  
}
