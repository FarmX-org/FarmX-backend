package io.farmx.service;

import io.farmx.dto.CropDto;
import io.farmx.model.Crop;
import io.farmx.model.Farm;
import io.farmx.model.UserEntity;
import io.farmx.repository.CropRepository;
import io.farmx.repository.FarmRepository;
import io.farmx.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService {
    private final CropRepository cropRepo;
    private final FarmRepository farmRepo;
    private final UserRepository userRepo;

    public CropService(CropRepository cropRepo, FarmRepository farmRepo, UserRepository userRepo) {
        this.cropRepo = cropRepo;
        this.farmRepo = farmRepo;
        this.userRepo = userRepo;
    }

    public List<CropDto> getCropsByFarm(Long farmId) {
        Farm farm = farmRepo.findById(farmId).orElseThrow();
        return cropRepo.findByFarm(farm).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CropDto saveCrop(CropDto dto) {
        Farm farm = farmRepo.findById(dto.getFarmId()).orElseThrow();
        Crop crop = fromDTO(dto, farm);
        return toDTO(cropRepo.save(crop));
    }

    public CropDto updateCrop(Long id, CropDto dto) {
        Crop crop = cropRepo.findById(id).orElseThrow();
        Farm farm = farmRepo.findById(dto.getFarmId()).orElseThrow();
        UserEntity user = userRepo.findById(id).orElseThrow();


        crop.setName(dto.getName());
        crop.setCategory(dto.getCategory());
        crop.setPrice(dto.getPrice());
        crop.setQuantity(dto.getQuantity());
        crop.setDescription(dto.getDescription());
        crop.setHarvestDate(dto.getHarvestDate());
        crop.setAvailable(dto.isAvailable());
        crop.setImageUrl(dto.getImageUrl());
        crop.setFarm(farm);

        return toDTO(cropRepo.save(crop));
    }

    public void deleteCrop(Long id) {
        cropRepo.deleteById(id);
    }

   /* public List<CropDto> getCropsForCurrentUser(String name) {
       /* UserEntity user = userRepo.findByUsername(name).orElseThrow();
        List<Farm> farms = farmRepo.findAllByUser(user);
        
        return farms.stream()
                    .flatMap(farm -> cropRepo.findByFarm(farm).stream())
                    .map(this::toDTO)
                    .collect(Collectors.toList());
    }
*/
    

    private CropDto toDTO(Crop crop) {
    	CropDto dto = new CropDto();
        dto.setId(crop.getId());
        dto.setName(crop.getName());
        dto.setCategory(crop.getCategory());
        dto.setPrice(crop.getPrice());
        dto.setQuantity(crop.getQuantity());
        dto.setDescription(crop.getDescription());
        dto.setHarvestDate(crop.getHarvestDate());
        dto.setAvailable(crop.isAvailable());
        dto.setImageUrl(crop.getImageUrl());
        dto.setFarmId(crop.getFarm().getId());
        return dto;
    }

    private Crop fromDTO(CropDto dto, Farm farm) {
        Crop crop = new Crop();
        crop.setId(dto.getId());
        crop.setName(dto.getName());
        crop.setCategory(dto.getCategory());
        crop.setPrice(dto.getPrice());
        crop.setQuantity(dto.getQuantity());
        crop.setDescription(dto.getDescription());
        crop.setHarvestDate(dto.getHarvestDate());
        crop.setAvailable(dto.isAvailable());
        crop.setImageUrl(dto.getImageUrl());
        crop.setFarm(farm);
        return crop;
    }
}
