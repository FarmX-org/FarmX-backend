package io.farmx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    @Column(length = 500)
    private String description;

    private String season;

    private int growthDays;

    private double averagePrice;

    @Column
    private String preferredSoilType;

    @Column
    private String preferredRegion;

    @Column
    private String temperatureSensitivity; // HIGH, MEDIUM, LOW

    @Column
    private String waterNeedLevel; // LOW, MEDIUM, HIGH

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreferredSoilType() {
        return preferredSoilType;
    }

    public void setPreferredSoilType(String preferredSoilType) {
        this.preferredSoilType = preferredSoilType;
    }

    public String getPreferredRegion() {
        return preferredRegion;
    }

    public void setPreferredRegion(String preferredRegion) {
        this.preferredRegion = preferredRegion;
    }

    public String getTemperatureSensitivity() {
        return temperatureSensitivity;
    }

    public void setTemperatureSensitivity(String temperatureSensitivity) {
        this.temperatureSensitivity = temperatureSensitivity;
    }

    public String getWaterNeedLevel() {
        return waterNeedLevel;
    }

    public void setWaterNeedLevel(String waterNeedLevel) {
        this.waterNeedLevel = waterNeedLevel;
    }

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
}
