package io.farmx.dto;

public class CropDto {
	 private Long id;
	    private String name;
	    private String category;
	    private double price;
	    private int quantity;
	    private String description;
	    private String harvestDate;
	    private boolean available;
	    private String imageUrl;
	    private Long farmId;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
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
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getHarvestDate() {
			return harvestDate;
		}
		public void setHarvestDate(String harvestDate) {
			this.harvestDate = harvestDate;
		}
		public boolean isAvailable() {
			return available;
		}
		public void setAvailable(boolean available) {
			this.available = available;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public Long getFarmId() {
			return farmId;
		}
		public void setFarmId(Long farmId) {
			this.farmId = farmId;
		}

}
