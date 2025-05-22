package io.farmx.controller;

import io.farmx.dto.CropDto;
import io.farmx.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/crops")
@CrossOrigin(origins = "*")
public class CropController {

    private final CropService service;

    public CropController(CropService service) {
        this.service = service;
    }

    @GetMapping("/farm/{farmId}")
    public List<CropDto> getByFarm(@PathVariable Long farmId) {
        return service.getCropsByFarm(farmId);
    }

    @PostMapping
    public CropDto create(@RequestBody CropDto crop) {
        return service.saveCrop(crop);
    }

    @PutMapping("/{id}")
    public CropDto update(@PathVariable Long id, @RequestBody CropDto crop) {
        return service.updateCrop(id, crop);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteCrop(id);
    }

   /* @GetMapping
    public ResponseEntity<List<CropDto>> getAllCrops(Principal principal) {
        String name = principal.getName();   
        return ResponseEntity.ok(service.getCropsForCurrentUser(name));
    }*/
}
