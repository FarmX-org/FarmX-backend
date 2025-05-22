package io.farmx.repository;

import org.springframework.data.repository.CrudRepository;

import io.farmx.model.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);
}