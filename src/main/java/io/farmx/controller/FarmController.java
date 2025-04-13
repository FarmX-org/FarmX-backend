package io.farmx.controller;

import io.farmx.dto.FarmDTO;
import io.farmx.service.FarmService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/farms")
@CrossOrigin(origins = "*")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping("/my")
    public List<FarmDTO> getMyFarms(Principal principal) {
        return farmService.getMyFarms(principal);
    }

    @PostMapping
    public FarmDTO create(@RequestBody FarmDTO dto, Principal principal) {
        return farmService.createFarm(dto, principal);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        farmService.deleteFarm(id, principal);
    }
}
