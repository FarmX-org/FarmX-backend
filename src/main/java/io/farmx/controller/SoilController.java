package io.farmx.controller;

import io.farmx.service.SoilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/soil")
public class SoilController {

    private final SoilService soilService;

    public SoilController(SoilService soilService) {
        this.soilService = soilService;
    }

    @GetMapping("/type")
    public ResponseEntity<String> getSoilType(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(soilService.getSoilTypeByCoordinates(lat, lon));
    }

    @GetMapping("/palestine-types")
    public ResponseEntity<List<Map<String, String>>> getPalestineSoilTypes() {
        List<Map<String, String>> types = new ArrayList<>();

        types.add(Map.of("code","SANDY","arabic","تربة رملية","region","النقب وغزة"));
        types.add(Map.of("code","CLAY","arabic","تربة طينية","region","وسط فلسطين"));
        types.add(Map.of("code","LOAMY","arabic","تربة طميية","region","الأغوار"));
        types.add(Map.of("code","CALCAREOUS","arabic","تربة جيرية","region","جبال الضفة"));
        types.add(Map.of("code","TERRA_ROSSA","arabic","تربة حمراء","region","جبال وسط"));
        types.add(Map.of("code","BASALTIC","arabic","تربة بازلتية","region","شمال شرق"));

        return ResponseEntity.ok(types);
    }
}
