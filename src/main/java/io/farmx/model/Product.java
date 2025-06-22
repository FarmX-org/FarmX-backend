package io.farmx.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "planted_crop_id", nullable = false)
    private PlantedCrop plantedCrop;


    private int quantity;


    private String unit;

    private double price;


    private boolean available;


    private LocalDate addedAt;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(length = 500)
    private String description; 


    private double rating;
    private int ratingCount;


    public Product() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlantedCrop getPlantedCrop() {
        return plantedCrop;
    }

    public void setPlantedCrop(PlantedCrop plantedCrop) {
        this.plantedCrop = plantedCrop;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




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

}
