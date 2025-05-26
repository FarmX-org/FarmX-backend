package io.farmx.dto;

import java.time.LocalDate;

public class PlantedCropDTO {
    private Long id;
    private Long cropId;
    private Long farmId;  
    private LocalDate plantedDate;
    private LocalDate estimatedHarvestDate;
    private LocalDate actualHarvestDate;
    private int quantity;
    private boolean available;
    private String notes;
    private String status;
    private String imageUrl;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCropId() {
		return cropId;
	}
	public void setCropId(Long cropId) {
		this.cropId = cropId;
	}
	public Long getFarmId() {
		return farmId;
	}
	public void setFarmId(Long farmId) {
		this.farmId = farmId;
	}
	public LocalDate getPlantedDate() {
		return plantedDate;
	}
	public void setPlantedDate(LocalDate plantedDate) {
		this.plantedDate = plantedDate;
	}
	public LocalDate getEstimatedHarvestDate() {
		return estimatedHarvestDate;
	}
	public void setEstimatedHarvestDate(LocalDate estimatedHarvestDate) {
		this.estimatedHarvestDate = estimatedHarvestDate;
	}
	public LocalDate getActualHarvestDate() {
		return actualHarvestDate;
	}
	public void setActualHarvestDate(LocalDate actualHarvestDate) {
		this.actualHarvestDate = actualHarvestDate;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

  
}
