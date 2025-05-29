package io.farmx.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import io.farmx.dto.ProductDTO;
import io.farmx.model.Farmer;
import io.farmx.model.PlantedCrop;
import io.farmx.model.Product;
import io.farmx.repository.PlantedCropRepository;
import io.farmx.repository.ProductRepository;
import io.farmx.repository.UserRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlantedCropRepository plantedCropRepository;

    @Autowired
    private UserRepository userRepository;

    public ProductDTO createProduct(ProductDTO dto, Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        PlantedCrop plantedCrop = plantedCropRepository.findById(dto.getPlantedCropId())
            .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

        // تأكد انه المنتج مرتبط بمزرعة المزارع نفسه
        if (!plantedCrop.getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this planted crop");
        }

        Product product = new Product();
        product.setPlantedCrop(plantedCrop);
        product.setQuantity(dto.getQuantity());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setAvailable(dto.isAvailable());
        product.setAddedAt(dto.getAddedAt() != null ? dto.getAddedAt() : LocalDate.now());
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());

        Product saved = productRepository.save(product);
        dto.setId(saved.getId());

        return dto;
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto, Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!existing.getPlantedCrop().getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this product");
        }

      
        if (dto.getPlantedCropId() != null &&
            !dto.getPlantedCropId().equals(existing.getPlantedCrop().getId())) {
            PlantedCrop newPlantedCrop = plantedCropRepository.findById(dto.getPlantedCropId())
                .orElseThrow(() -> new IllegalArgumentException("Planted crop not found"));

            if (!newPlantedCrop.getFarm().getFarmer().getId().equals(farmer.getId())) {
                throw new AccessDeniedException("You don't own the new planted crop");
            }

            existing.setPlantedCrop(newPlantedCrop);
        }

        existing.setQuantity(dto.getQuantity());
        existing.setUnit(dto.getUnit());
        existing.setPrice(dto.getPrice());
        existing.setAvailable(dto.isAvailable());
        existing.setAddedAt(dto.getAddedAt() != null ? dto.getAddedAt() : existing.getAddedAt());
        existing.setImageUrl(dto.getImageUrl());
        existing.setDescription(dto.getDescription());

        productRepository.save(existing);
        dto.setId(existing.getId());

        return dto;
    }

    public void deleteProduct(Long id, Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!existing.getPlantedCrop().getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this product");
        }

        productRepository.delete(existing);
    }

    public ProductDTO getProduct(Long id, Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!existing.getPlantedCrop().getFarm().getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You don't own this product");
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(existing.getId());
        dto.setPlantedCropId(existing.getPlantedCrop().getId());
        dto.setCropName(existing.getPlantedCrop().getCrop().getName());
        dto.setCategory(existing.getPlantedCrop().getCrop().getCategory());
        dto.setQuantity(existing.getQuantity());
        dto.setUnit(existing.getUnit());
        dto.setPrice(existing.getPrice());
        dto.setAvailable(existing.isAvailable());
        dto.setAddedAt(existing.getAddedAt());
        dto.setImageUrl(existing.getImageUrl());
        dto.setDescription(existing.getDescription());

        return dto;
    }

    public List<ProductDTO> getAllProducts(Principal principal) {
        String username = principal.getName();
        Farmer farmer = (Farmer) userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found or not a farmer"));

        List<Product> products = productRepository.findAllByPlantedCrop_Farm_Farmer_Id(farmer.getId());

        return products.stream().map(p -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setPlantedCropId(p.getPlantedCrop().getId());
            dto.setCropName(p.getPlantedCrop().getCrop().getName());
            dto.setCategory(p.getPlantedCrop().getCrop().getCategory());
            dto.setQuantity(p.getQuantity());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setAvailable(p.isAvailable());
            dto.setAddedAt(p.getAddedAt());
            dto.setImageUrl(p.getImageUrl());
            dto.setDescription(p.getDescription());
            return dto;
        }).toList();
    }
}
