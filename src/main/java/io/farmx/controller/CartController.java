package io.farmx.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.farmx.dto.CartDTO;
import io.farmx.dto.CartItemDTO;
import io.farmx.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody CartItemDTO itemDTO, Principal principal) {
        CartDTO updatedCart = cartService.addItemToCart(itemDTO, principal);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody CartItemDTO itemDTO,
            Principal principal) {
        CartDTO updatedCart = cartService.updateItemQuantity(itemId, itemDTO, principal);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId, Principal principal) {
        cartService.deleteItem(itemId, principal);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        cartService.clearCart(principal);
        return ResponseEntity.noContent().build();
    }

}

