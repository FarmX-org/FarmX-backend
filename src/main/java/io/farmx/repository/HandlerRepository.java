package io.farmx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.farmx.model.Handler;

public interface HandlerRepository extends JpaRepository<Handler, Long> {
   
}
