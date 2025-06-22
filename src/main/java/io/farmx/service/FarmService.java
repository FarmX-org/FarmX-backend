package io.farmx.service;

import io.farmx.dto.FarmDTO;
import io.farmx.enums.FarmStatus;
import io.farmx.enums.NotificationType;
import io.farmx.enums.OrderStatus;
import io.farmx.model.Farm;
import io.farmx.model.Farmer;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.FarmRepository;
import io.farmx.repository.NotificationRepository;
import io.farmx.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmService {

    private static final Logger logger = LoggerFactory.getLogger(FarmService.class);

    private final FarmRepository farmRepo;
    private final UserRepository userRepo;
    private final SoilService soilService;

    @Autowired
    private FCMService fcmService;
    @Autowired
    private NotificationRepository notificationRepository;
    public FarmService(FarmRepository farmRepo, UserRepository userRepo, SoilService soilService) {
        this.farmRepo = farmRepo;
        this.userRepo = userRepo;
        this.soilService = soilService;
    }

    public List<FarmDTO> getFarms(Principal principal) {
        String username = principal.getName();
        logger.info("Fetching farms for user '{}'", username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found", username);
                    return new IllegalArgumentException("User not found");
                });

        List<FarmDTO> farms;

        if (user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"))) {
            farms = farmRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
            if(farms.isEmpty()) {
                logger.warn("No farms found in the system.");
            }
        } else if (user instanceof Farmer farmer) {
            farms = farmRepo.findAllByFarmer(farmer).stream().map(this::toDto).collect(Collectors.toList());
            if(farms.isEmpty()) {
                logger.warn("No farms found for farmer '{}'", username);
            }
        } else {
            logger.error("User '{}' is not authorized to access farms", username);
            throw new AccessDeniedException("Unauthorized to access farms");
        }

        logger.info("Returning {} farms for user '{}'", farms.size(), username);
        return farms;
    }

    public FarmDTO createFarm(FarmDTO dto, Principal principal) {
    	 String soilTypeFromCoords = soilService.getSoilTypeByCoordinates(dto.getLatitude(), dto.getLongitude());
    
        String username = principal.getName();
        logger.info("Creating farm '{}' for user '{}'", dto.getName(), username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found", username);
                    return new IllegalArgumentException("User not found");
                });

        if (!(user instanceof Farmer farmer)) {
            logger.error("User '{}' is not authorized to create farms", username);
            throw new AccessDeniedException("Only farmers can create farms");
        }

        Farm farm = fromDto(dto);
        farm.setFarmer(farmer);
        dto.setSoilType(soilTypeFromCoords);
        farm.setStatus(FarmStatus.PENDING);
        Farm savedFarm = farmRepo.save(farm);
        logger.info("Farm '{}' created successfully with id {}", savedFarm.getName(), savedFarm.getId());
        return toDto(savedFarm);
    }

    public void deleteFarm(Long id, Principal principal) {
        String username = principal.getName();
        logger.info("Deleting farm with id {} requested by user '{}'", id, username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found", username);
                    return new IllegalArgumentException("User not found");
                });

        Farm farm = farmRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm with id {} not found", id);
                    return new NullPointerException("Farm not found");
                });

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
        boolean isOwner = (user instanceof Farmer farmer) && farm.getFarmer().getId().equals(farmer.getId());

        if (isAdmin || isOwner) {
            farmRepo.delete(farm);
            logger.info("Farm with id {} deleted successfully", id);
        } else {
            logger.error("User '{}' is not authorized to delete farm with id {}", username, id);
            throw new AccessDeniedException("Unauthorized to delete this farm");
        }
    }

    public FarmDTO updateFarm(Long id, FarmDTO dto, Principal principal) {
    	
    
        String username = principal.getName();
        logger.info("Updating farm with id {} requested by user '{}'", id, username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found", username);
                    return new IllegalArgumentException("User not found");
                });

        Farm farm = farmRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Farm with id {} not found", id);
                    return new NullPointerException("Farm not found");
                });

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
        boolean isOwner = (user instanceof Farmer farmer) && farm.getFarmer().getId().equals(farmer.getId());
          String soilTypeFromCoords = soilService.getSoilTypeByCoordinates(dto.getLatitude(), dto.getLongitude());
          dto.setSoilType(soilTypeFromCoords);
        if (isAdmin || isOwner) {
            farm.setName(dto.getName());
            farm.setLatitude(dto.getLatitude());
            farm.setLongitude(dto.getLongitude());
            farm.setAreaSize(dto.getAreaSize());
            farm.setSoilType(dto.getSoilType());
            farm.setLicenseDocumentUrl(dto.getLicenseDocumentUrl());

            Farm updatedFarm = farmRepo.save(farm);
            logger.info("Farm with id {} updated successfully", id);
            return toDto(updatedFarm);
        } else {
            logger.error("User '{}' is not authorized to update farm with id {}", username, id);
            throw new AccessDeniedException("Unauthorized to update this farm");
        }
    }

    public FarmDTO rateFarm(Long farmId, double newRating) {
        logger.info("Adding rating {} to farm with id {}", newRating, farmId);

        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> {
                    logger.error("Farm with id {} not found", farmId);
                    return new NullPointerException("Farm not found");
                });

        farm.addRating(newRating);

        Farm savedFarm = farmRepo.save(farm);
        logger.info("Rating added successfully to farm with id {}", farmId);
        return toDto(savedFarm);
    }

    public FarmDTO getFarmById(Long id, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Farm farm = farmRepo.findById(id)
                .orElseThrow(() -> new NullPointerException("Farm not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"));
        boolean isOwner = (user instanceof Farmer farmer) && farm.getFarmer().getId().equals(farmer.getId());

        if (isAdmin || isOwner) {
            return toDto(farm);
        } else {
            throw new AccessDeniedException("Unauthorized to access this farm");
        }
    }
    
    public FarmDTO changeFarmStatus(Long farmId, FarmStatus newStatus, String rejectionReason) {
        logger.info("Changing status of farm {} to {}", farmId, newStatus);

        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> {
                    logger.error("Farm with id {} not found", farmId);
                    return new NullPointerException("Farm not found");
                });

        farm.setStatus(newStatus);
        if (newStatus == FarmStatus.REJECTED) {
            farm.setRejectionReason(rejectionReason);
        } else {
            farm.setRejectionReason(null);
        }

        Farm updatedFarm = farmRepo.save(farm);
        logger.info("Farm {} status updated to {}", farmId, newStatus);

        UserEntity farmer = farm.getFarmer();

        String title;
        String message;
        NotificationType type;

        if (newStatus == FarmStatus.APPROVED) {
            title = "Farm Approved!";
            message = "Your farm \"" + farm.getName() + "\" has been approved.";
            type = NotificationType.FARM_APPROVED;
        } else {
            title = "Farm Rejected 😕";
            message = "Your farm \"" + farm.getName() + "\" was rejected. Reason: " + rejectionReason;
            type = NotificationType.INFO;
        }

        // 💾 Save notification in DB
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRecipient(farmer);
        notificationRepository.save(notification);

        // 📲 Send push notification
        String fcmToken = farmer.getFcmToken();
        if (fcmToken != null && !fcmToken.isBlank()) {
            try {
                fcmService.sendNotificationToToken(title, message, fcmToken);
            } catch (Exception e) {
                logger.error("❌ Failed to send FCM notification: {}", e.getMessage());
            }
        }

        return toDto(updatedFarm);
    }


    private FarmDTO toDto(Farm farm) {
        return new FarmDTO(
                farm.getId(),
                farm.getName(),
                farm.getLatitude(),
                farm.getLongitude(),
                farm.getAreaSize(),
                farm.getSoilType(),
                farm.getLicenseDocumentUrl(),
                farm.getRating(),
                farm.getRatingCount(),
                farm.getStatus(),
                farm.getRejectionReason()
        );
    }

    private Farm fromDto(FarmDTO dto) {
        Farm farm = new Farm();
        farm.setId(dto.getId());
        farm.setName(dto.getName());
        farm.setLatitude(dto.getLatitude());
        farm.setLongitude(dto.getLongitude());
        farm.setAreaSize(dto.getAreaSize());
        farm.setSoilType(dto.getSoilType());
        farm.setLicenseDocumentUrl(dto.getLicenseDocumentUrl());
        farm.setRating(dto.getRating());
        farm.setRatingCount(dto.getRatingCount());
        farm.setStatus(dto.getStatus());
        farm.setRejectionReason(dto.getRejectionReason());
        return farm;
    }

}
