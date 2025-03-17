package io.farmx.repository;

import org.springframework.data.repository.CrudRepository;

import io.farmx.model.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}