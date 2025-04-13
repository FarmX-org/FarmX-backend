package io.farmx.controller;
import io.farmx.dto.FarmDTO;
import io.farmx.service.FarmService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class FarmController {
    @GetMapping("/hello")
    public Map<String, String> sayHello(@RequestParam String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + name);
        return response;
    }
    @PostMapping("/hello-body")
    public Map<String, String> sayHelloPost(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + name);
        return response;
    }

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping("/farms/my")
    public List<FarmDTO> getMyFarms(Principal principal) {
        return farmService.getMyFarms(principal);
    }

    @PostMapping("/farms")
    public FarmDTO create(@RequestBody FarmDTO dto, Principal principal) {
        return farmService.createFarm(dto, principal);
    }

    @DeleteMapping("/farms/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        farmService.deleteFarm(id, principal);
    }
}


