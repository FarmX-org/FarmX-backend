package io.farmx.repository;

import io.farmx.model.Farm;
import io.farmx.model.PlantedCrop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantedCropRepository extends JpaRepository<PlantedCrop, Long> {
    List<PlantedCrop> findByFarm(Farm farm);
}

