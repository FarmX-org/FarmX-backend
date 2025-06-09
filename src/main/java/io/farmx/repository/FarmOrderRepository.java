package io.farmx.repository;


import io.farmx.model.FarmOrder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmOrderRepository extends JpaRepository<FarmOrder, Long> {
	
	List<FarmOrder> findByFarmId(Long farmId);

}
