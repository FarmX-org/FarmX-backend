package io.farmx.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SoilService {

    private static final String SOILGRID_URL_TEMPLATE =
        "https://soilgrids.org/query?lon=%f&lat=%f&properties=clay,sand,silt,phh2o";

    private final RestTemplate restTemplate = new RestTemplate();

   
    public String getSoilTypeByCoordinates(double latitude, double longitude) {
        try {
            String url = String.format(SOILGRID_URL_TEMPLATE, longitude, latitude);
            String response = restTemplate.getForObject(url, String.class);
            return parseSoilTypeFromJson(response, latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
    }

 
    private String parseSoilTypeFromJson(String json, double lat, double lon) {
        JSONObject root = new JSONObject(json);
        JSONObject props = root.getJSONObject("properties");

        double clay  = props.getJSONObject("clay").getDouble("mean");
        double sand  = props.getJSONObject("sand").getDouble("mean");
        double silt  = props.getJSONObject("silt").getDouble("mean");
        double ph    = props.getJSONObject("phh2o").getDouble("mean");

        return classifySoilType(clay, sand, silt, ph, lat, lon);
    }


    private String classifySoilType(
            double clay, double sand, double silt, double ph,
            double lat, double lon) {

        // 1) Sandy: لو الرمل ≥ 65% والـ clay قليل < 20%
        if (sand >= 65 && clay < 20) {
            return "SANDY";  // تربة رملية
        }

        // 2) Clay: لو الطين ≥ 40%
        if (clay >= 40) {
            return "CLAY";   // تربة طينية
        }

        // 3) Loamy: مزيج متوازن (طين 10–30، رمل 20–50، طمي 20–50)
        if (clay >= 10 && clay <= 30 &&
            sand >= 20 && sand <= 50 &&
            silt >= 20 && silt <= 50) {
            return "LOAMY";  // تربة طميية
        }

        // 4) Calcareous: تربة جيرية، لو pH ≥ 7.8 (قلوية عالية)
        if (ph >= 7.8) {
            return "CALCAREOUS";  // تربة جيرية
        }

        // 5) Terra Rossa (تربة حمراء):
        // مناطق الجبال الوسطى بفلسطين: lat 31.4–32.5, lon 34.8–35.4
        if (lat >= 31.4 && lat <= 32.5 &&
            lon >= 34.8 && lon <= 35.4) {
            return "TERRA_ROSSA"; // تربة حمراء
        }

        // 6) Basaltic (تربة بازلتية):
        // مناطق الشمال الشرقي (بساطين / الجليل): lat 32.3–33.5, lon 35.4–35.8
        if (lat >= 32.3 && lat <= 33.5 &&
            lon >= 35.4 && lon <= 35.8) {
            return "BASALTIC";    // تربة بازلتية
        }

        // إذا ما انطبق شيء، نرجع Mixed
        return "MIXED";  // تربة مختلطة
    }
}
