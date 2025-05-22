package io.farmx.repository;

import io.farmx.model.Farm;
import io.farmx.model.Farmer;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {
	List<Farm> findAllByFarmer(Farmer farmer);


}
