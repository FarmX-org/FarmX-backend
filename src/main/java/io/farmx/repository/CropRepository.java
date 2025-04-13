
package io.farmx.repository;

import io.farmx.model.Crop;
import io.farmx.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarm(Farm farm);
}
