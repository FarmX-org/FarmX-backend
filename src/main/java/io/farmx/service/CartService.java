package io.farmx.service;

import io.farmx.dto.CartDTO;
import io.farmx.dto.CartItemDTO;
import io.farmx.model.*;
import io.farmx.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private void checkConsumerRole(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (!(user instanceof Consumer)) {
            throw new AccessDeniedException("Only consumers allowed");
        }
    }

    private Consumer getConsumer(Principal principal) {
        checkConsumerRole(principal);
        return (Consumer) userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public CartDTO getCart(Principal principal) {
        Consumer consumer = getConsumer(principal);
        Cart cart = cartRepository.findByConsumer(consumer).orElse(null);
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setConsumer(consumer);
            newCart.setTotalPrice(0);
            newCart.setItems(new ArrayList<>());
            cart = cartRepository.save(newCart);
        }

        return convertToDTO(cart);
    }

 // في الخدمة:
    public CartDTO addItemToCart(CartItemDTO itemDTO, Principal principal) {
        Consumer consumer = getConsumer(principal);
        Cart cart = cartRepository.findByConsumer(consumer).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setConsumer(consumer);
            cart.setTotalPrice(0);
            cart.setItems(new ArrayList<>());
        }

        Product product = productRepository.findById(itemDTO.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isAvailable()) {
            throw new IllegalArgumentException("Product is not available");
        }

        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(itemDTO.getProductId()))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + itemDTO.getQuantity());
            existingItem.setItemTotal(existingItem.getQuantity() * product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(itemDTO.getQuantity());
            newItem.setItemTotal(itemDTO.getQuantity() * product.getPrice());
            cart.getItems().add(newItem);
        }

        recalcCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    public CartDTO updateItemQuantity(Long itemId, CartItemDTO itemDTO, Principal principal) {
        Consumer consumer = getConsumer(principal);
        Cart cart = cartRepository.findByConsumer(consumer)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("Item does not belong to your cart");
        }

        item.setQuantity(itemDTO.getQuantity());
        item.setItemTotal(itemDTO.getQuantity() * item.getProduct().getPrice());
        cartItemRepository.save(item);

        recalcCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }


    public void deleteItem(Long itemId, Principal principal) {
        Consumer consumer = getConsumer(principal);
        Cart cart = cartRepository.findByConsumer(consumer)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("Item does not belong to your cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        recalcCartTotal(cart);
        cartRepository.save(cart);
    }

    private void recalcCartTotal(Cart cart) {
        double total = cart.getItems().stream()
            .mapToDouble(CartItem::getItemTotal)
            .sum();

        cart.setTotalPrice(total);
    }
    public void clearCart(Principal principal) {
        Consumer consumer = getConsumer(principal);
        Cart cart = cartRepository.findByConsumer(consumer)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems()); 
        cart.getItems().clear(); 
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }



    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        List<CartItemDTO> itemsDto = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            CartItemDTO itemDto = new CartItemDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getPlantedCrop().getCrop().getName());

            itemDto.setProductImage(item.getProduct().getImageUrl()); 
            itemDto.setProductPrice(item.getProduct().getPrice());

            itemDto.setQuantity(item.getQuantity());
            itemDto.setItemTotal(item.getItemTotal());
            itemsDto.add(itemDto);
        }
        dto.setItems(itemsDto);
        dto.setTotalPrice(cart.getTotalPrice());

        return dto;
    }

}
