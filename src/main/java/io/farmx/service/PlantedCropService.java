package io.farmx.service;

import io.farmx.dto.PlantedCropDTO;
import io.farmx.model.Crop;
import io.farmx.model.Farm;
import io.farmx.model.Farmer;
import io.farmx.model.PlantedCrop;
import io.farmx.repository.CropRepository;
import io.farmx.repository.FarmRepository;
import io.farmx.repository.PlantedCropRepository;
import io.farmx.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantedCropService {

    private final PlantedCropRepository plantedCropRepository;
    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    public PlantedCropService(PlantedCropRepository plantedCropRepository,
                              FarmRepository farmRepository,
                              CropRepository cropRepository,
                              UserRepository userRepository) {
        this.plantedCropRepository = plantedCropRepository;
        this.farmRepository = farmRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }

    public PlantedCropDTO addPlantedCrop(PlantedCropDTO dto, Principal principal) {
        String username = principal.getName();

        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        Farm farm = farmRepository.findById(dto.getFarmId())
                .orElseThrow(() -> new IllegalArgumentException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this farm");
        }

        Crop crop = cropRepository.findById(dto.getCropId())
                .orElseThrow(() -> new IllegalArgumentException("Crop not found"));

        PlantedCrop plantedCrop = new PlantedCrop();
        plantedCrop.setFarm(farm);
        plantedCrop.setCrop(crop);
        plantedCrop.setPlantedDate(dto.getPlantedDate() != null ? dto.getPlantedDate() : LocalDate.now());
        plantedCrop.setEstimatedHarvestDate(dto.getEstimatedHarvestDate());
        plantedCrop.setActualHarvestDate(dto.getActualHarvestDate());
        plantedCrop.setQuantity(dto.getQuantity());
        plantedCrop.setAvailable(dto.isAvailable());
        plantedCrop.setNotes(dto.getNotes());
        plantedCrop.setStatus(dto.getStatus());
        plantedCrop.setImageUrl(dto.getImageUrl());

        PlantedCrop saved = plantedCropRepository.save(plantedCrop);
        dto.setId(saved.getId());
        return dto;
    }

    public List<PlantedCropDTO> getPlantedCropsForFarm(Long farmId, Principal principal) {
        String username = principal.getName();

        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("Farm not found"));

        if (!farm.getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this farm");
        }

        return plantedCropRepository.findByFarm(farm).stream().map(pc -> {
            PlantedCropDTO dto = new PlantedCropDTO();
            dto.setId(pc.getId());
            dto.setCropId(pc.getCrop().getId());
            dto.setFarmId(pc.getFarm().getId());
            dto.setPlantedDate(pc.getPlantedDate());
            dto.setEstimatedHarvestDate(pc.getEstimatedHarvestDate());
            dto.setActualHarvestDate(pc.getActualHarvestDate());
            dto.setQuantity(pc.getQuantity());
            dto.setAvailable(pc.isAvailable());
            dto.setNotes(pc.getNotes());
            dto.setStatus(pc.getStatus());
            dto.setImageUrl(pc.getImageUrl());
            return dto;
        }).collect(Collectors.toList());
    }
    public PlantedCropDTO updatePlantedCrop(Long id, PlantedCropDTO dto, Principal principal) {
        String username = principal.getName();

        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        PlantedCrop existing = plantedCropRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

       if (!existing.getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this planted crop");
        }

       existing.setPlantedDate(dto.getPlantedDate() != null ? dto.getPlantedDate() : existing.getPlantedDate());
        existing.setEstimatedHarvestDate(dto.getEstimatedHarvestDate());
        existing.setActualHarvestDate(dto.getActualHarvestDate());
        existing.setQuantity(dto.getQuantity());
        existing.setAvailable(dto.isAvailable());
        existing.setNotes(dto.getNotes());
        existing.setStatus(dto.getStatus());
        existing.setImageUrl(dto.getImageUrl());

       if (dto.getCropId() != null && !dto.getCropId().equals(existing.getCrop().getId())) {
            Crop crop = cropRepository.findById(dto.getCropId())
                    .orElseThrow(() -> new IllegalArgumentException("Crop not found"));
            existing.setCrop(crop);
        }
        if (dto.getFarmId() != null && !dto.getFarmId().equals(existing.getFarm().getId())) {
            Farm farm = farmRepository.findById(dto.getFarmId())
                    .orElseThrow(() -> new IllegalArgumentException("Farm not found"));
            if (!farm.getFarmer().getId().equals(farmer.getId())) {
                throw new AccessDeniedException("You don't own the new farm");
            }
            existing.setFarm(farm);
        }

        PlantedCrop saved = plantedCropRepository.save(existing);

        dto.setId(saved.getId());
        return dto;
    }
    public void deletePlantedCrop(Long id, Principal principal) {
        String username = principal.getName();

        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        PlantedCrop existing = plantedCropRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

        if (!existing.getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this planted crop");
        }

        plantedCropRepository.delete(existing);
    }



  
}
