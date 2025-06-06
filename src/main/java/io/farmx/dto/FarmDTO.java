package io.farmx.dto;

import io.farmx.enums.FarmStatus;

public class FarmDTO {

    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private double areaSize;
    private String soilType;
    private String licenseDocumentUrl;
    private double rating;
    private int ratingCount;
    private FarmStatus status;
    private String rejectionReason;


    public FarmDTO(Long id, String name, double latitude, double longitude, double areaSize, String soilType,
                   String licenseDocumentUrl, double rating, int ratingCount, FarmStatus status, String rejectionReason) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaSize = areaSize;
        this.soilType = soilType;
        this.licenseDocumentUrl = licenseDocumentUrl;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }


    public FarmStatus getStatus() {
        return status;
    }

    public void setStatus(FarmStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAreaSize() { return areaSize; }
    public void setAreaSize(double areaSize) { this.areaSize = areaSize; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getLicenseDocumentUrl() { return licenseDocumentUrl; }
    public void setLicenseDocumentUrl(String licenseDocumentUrl) { this.licenseDocumentUrl = licenseDocumentUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}
