package io.farmx.controller;

import io.farmx.dto.CropDTO;
import io.farmx.service.CropService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/crops")
@CrossOrigin(origins = "*")
public class CropController {

	@Autowired 
    private CropService cropService;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public CropDTO create(@RequestBody CropDTO dto) {
        return cropService.createCrop(dto);
    }

    @GetMapping
    public List<CropDTO> getAll() {
        return cropService.getAllCrops();
    }

    @GetMapping("/{id}")
    public CropDTO getById(@PathVariable Long id) {
        return cropService.getCropById(id);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public CropDTO update(@PathVariable Long id, @RequestBody CropDTO dto) {
        return cropService.updateCrop(id, dto);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	cropService.deleteCrop(id);
    }
}
