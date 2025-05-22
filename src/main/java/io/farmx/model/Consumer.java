package io.farmx.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "consumers")
public class Consumer extends UserEntity {

    private String favoriteProduct; 

    public String getFavoriteProduct() {
        return favoriteProduct;
    }

    public void setFavoriteProduct(String favoriteProduct) {
        this.favoriteProduct = favoriteProduct;
    }
}
