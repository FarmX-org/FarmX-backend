package io.farmx.service;


import io.farmx.dto.CropDTO;
import io.farmx.enums.NotificationType;
import io.farmx.model.Crop;
import io.farmx.model.UserEntity;
import io.farmx.repository.CropRepository;
import io.farmx.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService {

    @Autowired private CropRepository repo;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;
private CropDTO toDto(Crop c) {
    CropDTO r = new CropDTO();
    r.setId(c.getId());
    r.setName(c.getName());
    r.setCategory(c.getCategory());
    r.setDescription(c.getDescription());
    r.setSeason(c.getSeason());
    r.setGrowthDays(c.getGrowthDays());
    r.setAveragePrice(c.getAveragePrice());
    r.setImageUrl(c.getImageUrl()); // 👈 أضف هذا
    return r;
}


    public CropDTO createCrop(CropDTO dto) {
        Crop c = new Crop();
        c.setName(dto.getName());
        c.setCategory(dto.getCategory());
        c.setDescription(dto.getDescription());
        c.setSeason(dto.getSeason());
        c.setGrowthDays(dto.getGrowthDays());
        c.setAveragePrice(dto.getAveragePrice());
        c.setImageUrl(dto.getImageUrl()); 
        return toDto(repo.save(c));
    }


    public List<CropDTO> getAllCrops() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }


    public CropDTO getCropById(Long id) {
        Crop c = repo.findById(id).orElseThrow(() -> new RuntimeException("Crop not found"));
        return toDto(c);
    }

    public CropDTO updateCrop(Long id, CropDTO dto) {
        Crop c = repo.findById(id).orElseThrow(() -> new RuntimeException("Crop not found"));
        c.setName(dto.getName());
        c.setCategory(dto.getCategory());
        c.setDescription(dto.getDescription());
        c.setSeason(dto.getSeason());
        c.setGrowthDays(dto.getGrowthDays());
        c.setAveragePrice(dto.getAveragePrice());
        c.setImageUrl(dto.getImageUrl()); // 👈 أضف هذا

        return toDto(repo.save(c));
    }


    public void deleteCrop(Long id) {
        repo.deleteById(id);
    }
}
