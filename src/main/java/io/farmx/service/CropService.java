package io.farmx.service;


import io.farmx.dto.CropDTO;
import io.farmx.enums.NotificationType;
import io.farmx.model.Crop;

import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.CropRepository;
import io.farmx.repository.NotificationRepository;

import io.farmx.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.Principal;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService {

    @Autowired private CropRepository repo;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowieed private UserRepository userRepository;
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
    r.setPreferredSoilType(c.getPreferredSoilType());
    r.setPreferredRegion(c.getPreferredRegion());
    r.setTemperatureSensitivity(c.getTemperatureSensitivity());
    r.setWaterNeedLevel(c.getWaterNeedLevel());
    r.setImageUrl(c.getImageUrl()); // 👈 أضف هذا
    return r;
}


    public CropDTO createCrop(CropDTO dto,Principal principal) {
        String username = principal.getName();
          UserEntity user = userRepository.findByUsername(username)
                  .orElseThrow(() -> {
                      return new IllegalArgumentException("User not found");
                  });
        Crop c = new Crop();
        c.setName(dto.getName());
        c.setCategory(dto.getCategory());
        c.setDescription(dto.getDescription());
        c.setSeason(dto.getSeason());
        c.setGrowthDays(dto.getGrowthDays());
        c.setAveragePrice(dto.getAveragePrice());
         c.setPreferredSoilType(dto.getPreferredSoilType());
        c.setPreferredRegion(dto.getPreferredRegion());
        c.setTemperatureSensitivity(dto.getTemperatureSensitivity());
        c.setWaterNeedLevel(dto.getWaterNeedLevel());
        c.setImageUrl(dto.getImageUrl()); 

        Crop savedCrop = repo.save(c); // ✅ Save crop

        // ✅ Create & save in DB
        Notification notification = new Notification();
        notification.setTitle("Crop Added");
        notification.setMessage("You successfully added crop: " + savedCrop.getName());
        notification.setType(NotificationType.INFO);
        notification.setRecipient(user);
        notificationRepository.save(notification); // 💾 Save in DB

        // ✅ Send via FCM
        String fcmToken = user.getFcmToken(); // Make sure this is stored
        if (fcmToken != null && !fcmToken.isBlank()) {
            try {
                fcmService.sendNotificationToToken(
                    notification.getTitle(),
                    notification.getMessage(),
                    fcmToken
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to send FCM notification: " + e.getMessage());
            }
        }

        return toDto(savedCrop);
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

        c.setPreferredSoilType(dto.getPreferredSoilType());
        c.setPreferredRegion(dto.getPreferredRegion());
        c.setTemperatureSensitivity(dto.getTemperatureSensitivity());
        c.setWaterNeedLevel(dto.getWaterNeedLevel());

        c.setImageUrl(dto.getImageUrl()); // 👈 أضف هذا
        return toDto(repo.save(c));
    }


    public void deleteCrop(Long id) {
        repo.deleteById(id);
    }
}
