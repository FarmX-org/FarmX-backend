package io.farmx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.farmx.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
