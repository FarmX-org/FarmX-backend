package io.farmx.repository;


import io.farmx.model.Farm;

import io.farmx.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByPlantedCrop_Farm_Farmer_Id(Long farmerId);
    List<Product> findByAvailableTrue();

    List<Product> findByPlantedCrop_FarmIn(List<Farm> farms);


}
