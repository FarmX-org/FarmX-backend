package io.farmx.service;

import io.farmx.model.Crop;
import io.farmx.model.Farm;
import io.farmx.repository.CropRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

	@Autowired
    private  CropRepository cropRepository;

    public RecommendationService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public List<Crop> recommendCropsForFarm(Farm farm) {
        String currentSeason = getCurrentSeason(); // من التاريخ الحالي
        String soilType = farm.getSoilType();

        return cropRepository.findAll().stream()
                .filter(crop -> crop.getSeason().equalsIgnoreCase(currentSeason))
                .filter(crop -> crop.getPreferredSoilType().equalsIgnoreCase(soilType))
                .collect(Collectors.toList());
    }

    private String getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) return "Spring";
        if (month >= 6 && month <= 8) return "Summer";
        if (month >= 9 && month <= 11) return "Autumn";
        return "Winter";
    }
}
