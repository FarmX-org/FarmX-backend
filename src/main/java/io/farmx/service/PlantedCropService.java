package io.farmx.service;

import io.farmx.dto.FarmCropsDTO;
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
import java.time.LocalDateTime;
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

        LocalDate plantedDate = dto.getPlantedDate() != null ? dto.getPlantedDate() : LocalDate.now();
        plantedCrop.setPlantedDate(plantedDate);

        // حسب موعد الحصاد التقديري بناءً على plantedDate و growthDays
        LocalDate estimatedHarvestDate = plantedDate.plusDays(crop.getGrowthDays());
        plantedCrop.setEstimatedHarvestDate(estimatedHarvestDate);

        plantedCrop.setActualHarvestDate(dto.getActualHarvestDate());
        plantedCrop.setQuantity(dto.getQuantity());
        plantedCrop.setAvailable(dto.isAvailable());
        plantedCrop.setNotes(dto.getNotes());
        plantedCrop.setStatus(dto.getStatus());
        plantedCrop.setImageUrl(dto.getImageUrl());

        // آخر مرة تسميد - جديد ما في، خلينا null أو حسب dto لو عندك
        plantedCrop.setFertilizedAt(dto.getFertilizedAt());

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

        LocalDate newPlantedDate = dto.getPlantedDate() != null ? dto.getPlantedDate() : existing.getPlantedDate();

        Crop crop;
        if (dto.getCropId() != null && !dto.getCropId().equals(existing.getCrop().getId())) {
            crop = cropRepository.findById(dto.getCropId())
                    .orElseThrow(() -> new IllegalArgumentException("Crop not found"));
            existing.setCrop(crop);
        } else {
            crop = existing.getCrop();
        }

        existing.setPlantedDate(newPlantedDate);

        // حسب موعد الحصاد مجددًا مع تحديث plantedDate أو crop
        LocalDate estimatedHarvestDate = newPlantedDate.plusDays(crop.getGrowthDays());
        existing.setEstimatedHarvestDate(estimatedHarvestDate);

        existing.setActualHarvestDate(dto.getActualHarvestDate());
        existing.setQuantity(dto.getQuantity());
        existing.setAvailable(dto.isAvailable());
        existing.setNotes(dto.getNotes());
        existing.setStatus(dto.getStatus());
        existing.setImageUrl(dto.getImageUrl());

        // التسميد - تحديث لو موجود
        if (dto.getFertilizedAt() != null) {
            existing.setFertilizedAt(dto.getFertilizedAt());
        }

        // تحديث المزرعة إذا تغيرت (تأكد الملكية)
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
    
    public List<PlantedCropDTO> getAllPlantedCrops() {
        List<PlantedCrop> crops = plantedCropRepository.findAll();

        return crops.stream().map(pc -> {
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
            dto.setFarmName(pc.getFarm().getName());
            dto.setFarmerName(pc.getFarm().getFarmer().getName()); 
            return dto;
        }).collect(Collectors.toList());
    }

    public List<FarmCropsDTO> getAllCropsByFarmer(Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        List<Farm> farms = farmRepository.findAllByFarmer(farmer);

        
        return farms.stream().map(farm -> {
            List<PlantedCropDTO> cropsDtos = plantedCropRepository.findByFarm(farm)
                .stream()
                .map(pc -> {
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

            return new FarmCropsDTO(farm.getId(), farm.getName(), cropsDtos);
        }).collect(Collectors.toList());
    }

    // تحديث التسميد فقط - API مخصص لتحديث fertilizedAt
    public void fertilizeCrop(Long plantedCropId, Principal principal) {
        String username = principal.getName();

        Farmer farmer = (Farmer) userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        PlantedCrop existing = plantedCropRepository.findById(plantedCropId)
                .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

        if (!existing.getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this planted crop");
        }

        existing.setFertilizedAt(java.time.LocalDateTime.now());

        plantedCropRepository.save(existing);
    }

    public boolean needsFertilization(PlantedCropDTO dto) {
        if (dto == null || dto.getCropId() == null) return false;

        Crop crop = cropRepository.findById(dto.getCropId())
            .orElse(null);

        if (crop == null) return false;

        Integer freq = crop.getFertilizationFrequencyDays();

        if (freq == null || freq <= 0) return false;

        if (dto.getFertilizedAt() == null) return true;

        long daysSinceLast = java.time.Duration.between(
            dto.getFertilizedAt(),
            LocalDateTime.now()
        ).toDays();

        return daysSinceLast >= freq;
    }


    public PlantedCropDTO getPlantedCropById(Long id, Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        PlantedCrop pc = plantedCropRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

        if (!pc.getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this planted crop");
        }

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
        dto.setFertilizedAt(pc.getFertilizedAt()); // مهم!

        return dto;
    }

  
}
