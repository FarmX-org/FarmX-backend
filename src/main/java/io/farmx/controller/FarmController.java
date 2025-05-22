package io.farmx.controller;

import io.farmx.dto.FarmDTO;
import io.farmx.service.FarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @GetMapping
    public ResponseEntity<List<FarmDTO>> getFarms(Principal principal) {
        List<FarmDTO> farms = farmService.getFarms(principal);
        return ResponseEntity.ok(farms);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @GetMapping("/{id}")
    public ResponseEntity<FarmDTO> getFarmById(@PathVariable Long id, Principal principal) {
        FarmDTO farm = farmService.getFarmById(id, principal);
        return ResponseEntity.ok(farm);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @PostMapping
    public ResponseEntity<FarmDTO> createFarm(@RequestBody FarmDTO dto, Principal principal) {
        FarmDTO createdFarm = farmService.createFarm(dto, principal);
        return ResponseEntity.ok(createdFarm);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @PutMapping("/{id}")
    public ResponseEntity<FarmDTO> updateFarm(@PathVariable Long id, @RequestBody FarmDTO dto, Principal principal) {
        FarmDTO updatedFarm = farmService.updateFarm(id, dto, principal);
        return ResponseEntity.ok(updatedFarm);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarm(@PathVariable Long id, Principal principal) {
        farmService.deleteFarm(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @PostMapping("/{id}/rate")
    public ResponseEntity<FarmDTO> rateFarm(@PathVariable Long id, @RequestParam double rating) {
        FarmDTO ratedFarm = farmService.rateFarm(id, rating);
        return ResponseEntity.ok(ratedFarm);
    }
}
