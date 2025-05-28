package io.farmx.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileDTO {

    private String username;
    private String name;
    private String phone;
    private String city;
    private String street;
    private String email;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;

    // Constructors

    public UserProfileDTO() {}

    public UserProfileDTO(String username, String name, String phone, String city,
            String street, String email, String profilePhotoUrl,
            LocalDateTime createdAt, LocalDateTime updatedAt,
            List<String> roles) {
this.username = username;
this.name = name;
this.phone = phone;
this.city = city;
this.street = street;
this.email = email;
this.profilePhotoUrl = profilePhotoUrl;
this.createdAt = createdAt;
this.updatedAt = updatedAt;
this.roles = roles;
}

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
