package io.farmx.dto;

import java.util.List;

public class FarmCropsDTO {
    private Long farmId;
    private String farmName;
    private List<PlantedCropDTO> plantedCrops;

    public FarmCropsDTO() {}

    public FarmCropsDTO(Long farmId, String farmName, List<PlantedCropDTO> plantedCrops) {
        this.farmId = farmId;
        this.farmName = farmName;
        this.plantedCrops = plantedCrops;
    }

    
    public Long getFarmId() { return farmId; }
    public void setFarmId(Long farmId) { this.farmId = farmId; }

    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }

    public List<PlantedCropDTO> getPlantedCrops() { return plantedCrops; }
    public void setPlantedCrops(List<PlantedCropDTO> plantedCrops) { this.plantedCrops = plantedCrops; }
}
