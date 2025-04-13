package io.farmx.repository;

import io.farmx.model.Farm;
import io.farmx.model.UserEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findAllByUser(UserEntity user);

}
