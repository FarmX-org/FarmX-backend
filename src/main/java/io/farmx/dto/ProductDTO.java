package io.farmx.dto;

import java.time.LocalDate;

public class ProductDTO {
	private Long id;
    private Long plantedCropId;
    private String cropName;
    private int quantity;
    private String unit;
    private double price;
    private boolean available;
    private LocalDate addedAt;
    private String description;
    private String imageUrl;
    private String category;
    private double rating;
    private int ratingCount;

    
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public int getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPlantedCropId() {
		return plantedCropId;
	}
	public void setPlantedCropId(Long plantedCropId) {
		this.plantedCropId = plantedCropId;
	}
	public String getCropName() {
		return cropName;
	}
	public void setCropName(String cropName) {
		this.cropName = cropName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public LocalDate getAddedAt() {
		return addedAt;
	}
	public void setAddedAt(LocalDate addedAt) {
		this.addedAt = addedAt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
    
}
