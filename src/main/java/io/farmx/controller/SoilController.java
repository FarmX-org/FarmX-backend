package io.farmx.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.service.SoilService;

@RestController
@RequestMapping("/soil")
public class SoilController {

    private final SoilService soilService;

    public SoilController(SoilService soilService) {
        this.soilService = soilService;
    }

    @GetMapping("/type")
    public ResponseEntity<String> getSoilType(
            @RequestParam double lat,
            @RequestParam double lon) {
        String soilType = soilService.getSoilTypeByCoordinates(lat, lon);
        return ResponseEntity.ok(soilType);
    }
    
    @GetMapping("/palestine-types")
    public ResponseEntity<List<Map<String, String>>> getPalestineSoilTypes() {
        List<Map<String, String>> types = new ArrayList<>();

        types.add(Map.of(
            "code", "SANDY",
            "arabic", "تربة رملية",
            "region", "النقب وغزة (سواحل وجنوب)"
        ));
        types.add(Map.of(
            "code", "CLAY",
            "arabic", "تربة طينية",
            "region", "وسط فلسطين (سهول الضفة)"
        ));
        types.add(Map.of(
            "code", "LOAMY",
            "arabic", "تربة طميية",
            "region", "الأغوار وجنين"
        ));
        types.add(Map.of(
            "code", "CALCAREOUS",
            "arabic", "تربة جيرية",
            "region", "جبال الضفة (الكربونات)"
        ));
        types.add(Map.of(
            "code", "TERRA_ROSSA",
            "arabic", "تربة حمراء",
            "region", "جبال فلسطين الوسطى (القدس، الخليل)"
        ));
        types.add(Map.of(
            "code", "BASALTIC",
            "arabic", "تربة بازلتية",
            "region", "شمال شرق فلسطين (منطقة بيسان والجليل)"
        ));

        return ResponseEntity.ok(types);
    }
}
