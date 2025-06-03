package io.farmx.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.dto.ProductDTO;
import io.farmx.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto, Principal principal) {
        ProductDTO created = productService.createProduct(dto, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id, Principal principal) {
        ProductDTO dto = productService.getProduct(id, principal);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(Principal principal) {
        List<ProductDTO> list = productService.getAllProducts(principal);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('FARMER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto, Principal principal) {
        ProductDTO updated = productService.updateProduct(id, dto, principal);
        return ResponseEntity.ok(updated);
    }
    @PreAuthorize("hasRole('FARMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {
        productService.deleteProduct(id, principal);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/store")
    public ResponseEntity<List<ProductDTO>> getAvailableProductsForStore() {
        List<ProductDTO> list = productService.getAvailableProductsForStore();
        return ResponseEntity.ok(list);
    }

}
