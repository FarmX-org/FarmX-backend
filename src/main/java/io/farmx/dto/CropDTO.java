
package io.farmx.dto;

public class CropDTO {
	private Long id;
    private String name;
    private String category;
    private String description;
    private String season;
    private int growthDays;
    private double averagePrice;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public int getGrowthDays() {
		return growthDays;
	}
	public void setGrowthDays(int growthDays) {
		this.growthDays = growthDays;
	}
	public double getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
   
}
