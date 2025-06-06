package io.farmx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.farmx.model.Cart;
import io.farmx.model.Consumer;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByConsumer(Consumer consumer);
}
