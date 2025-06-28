package io.farmx.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SoilService {

    private static final String SOILGRID_URL_TEMPLATE =
        "https://soilgrids.org/query?lon=%f&lat=%f&properties=clay,sand,silt,phh2o";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getSoilTypeByCoordinates(double lat, double lon) {
        try {
            String url = String.format(SOILGRID_URL_TEMPLATE, lon, lat);
            String resp = restTemplate.getForObject(url, String.class);
            return parseSoilTypeFromJson(resp, lat, lon);
        } catch (Exception e) {
            e.printStackTrace();
            return classifyFallbackSoilType(lat, lon);
        }
    }

    private String parseSoilTypeFromJson(String json, double lat, double lon) {
        JSONObject root = new JSONObject(json);
        JSONObject props = root.getJSONObject("properties");

        double clay = props.getJSONObject("clay").getDouble("mean");
        double sand = props.getJSONObject("sand").getDouble("mean");
        double silt = props.getJSONObject("silt").getDouble("mean");
        double ph   = props.getJSONObject("phh2o").getDouble("mean");

        return classifyByData(clay, sand, silt, ph, lat, lon);
    }

    private String classifyByData(double clay, double sand, double silt, double ph, double lat, double lon) {
        if (sand >= 65 && clay < 20) return "SANDY";
        if (clay >= 40) return "CLAY";
        if (clay >= 10 && clay <= 30 && sand >= 20 && sand <= 50 && silt >= 20 && silt <= 50) return "LOAMY";
        if (ph >= 7.8) return "CALCAREOUS";

        return classifyFallbackSoilType(lat, lon);
    }

    private String classifyFallbackSoilType(double lat, double lon) {
        // مناطق غزة والساحل (رملية ساحلية أو كلسية)
        if (lat >= 31.2 && lat <= 32.4 && lon >= 34.2 && lon <= 34.6) {
            return "ARENOSOLIC"; // تربة رملية
        }

        // سهول طولكرم، قلقيلية، طوباس
        if (lat >= 32.2 && lat <= 32.4 && lon >= 34.8 && lon <= 35.3) {
            return "TERRA_ROSSA"; // تربة حمراء
        }

        // جبال الضفة الغربية (نابلس، رام الله، الخليل، القدس)
        if (lat >= 31.3 && lat <= 32.5 && lon >= 34.8 && lon <= 35.4) {
            return "RENDZINA"; // تربة رقيقة كلسية
        }

        // المنحدرات الشرقية (بيت لحم الشرقية، شرق نابلس)
        if (lat >= 31.5 && lat <= 32.3 && lon >= 35.3 && lon <= 35.5) {
            return "GREY_CALCAREOUS_STEPPE"; // تربة رمادية كلسية
        }

        // الأغوار وأريحا
        if (lat >= 31.7 && lat <= 32.3 && lon >= 35.4 && lon <= 35.6) {
            return "REGOSOL"; // تربة رملية نهرية
        }

        // مناطق مالحة في الأغوار (قريبة من البحر الميت)
        if (lat >= 31.3 && lat <= 31.6 && lon >= 35.4 && lon <= 35.6) {
            return "SOLONCHAK"; // تربة ملحية
        }

        // الجليل الأعلى وبقعة بيسان
        if (lat >= 32.6 && lat <= 33.3 && lon >= 35.4 && lon <= 35.8) {
            return "BASALTIC"; // تربة بازلتية
        }

        // النقب، جنوب الخليل، مناطق صحراوية
        if (lat <= 31.3 && lon <= 35.0) {
            return "SANDY_REGOSOL"; // تربة رملية جافة
        }

        return "MIXED"; // fallback إذا مش معروف
    }
}
