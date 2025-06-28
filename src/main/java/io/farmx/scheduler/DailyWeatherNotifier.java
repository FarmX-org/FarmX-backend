package io.farmx.scheduler;

import io.farmx.enums.NotificationType;
import io.farmx.model.Farm;
import io.farmx.model.Notification;
import io.farmx.repository.FarmRepository;
import io.farmx.repository.NotificationRepository;
import io.farmx.service.FCMService;
import io.farmx.service.NotificationService;
import io.farmx.service.WeatherService;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyWeatherNotifier {

    private final WeatherService weatherService;
    private final FarmRepository farmRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    public DailyWeatherNotifier(
            WeatherService weatherService,
            FarmRepository farmRepository,
            NotificationService notificationService,
            NotificationRepository notificationRepository,
            FCMService fcmService) {
        this.weatherService = weatherService;
        this.farmRepository = farmRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.fcmService = fcmService;
    }

    @Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Gaza")  // أو "Asia/Jerusalem"
    public void sendDailyWeatherAlerts() {
        List<Farm> farms = farmRepository.findAll();
        for (Farm farm : farms) {
            try {
                String weatherJson = weatherService.getWeatherData(farm.getLatitude(), farm.getLongitude());
                JSONObject obj = new JSONObject(weatherJson);

                String condition = obj.getJSONArray("weather").getJSONObject(0).getString("main");
                double temp = obj.getJSONObject("main").getDouble("temp");

                StringBuilder message = new StringBuilder();
                message.append(String.format("Today's weather: %s, %.1f°C. ", condition, temp));

                // شروط الطقس الذكية
                if (condition.equalsIgnoreCase("Rain")) {
                    message.append("Rain expected, no need to irrigate.");
                } else if (condition.equalsIgnoreCase("Clear") && temp > 30) {
                    message.append("Sunny and hot, watch your sensitive crops.");
                } else if (condition.equalsIgnoreCase("Fog")) {
                    message.append("Foggy weather, drive carefully.");
                } else {
                    message.append("Normal weather, manage irrigation accordingly.");
                }

                // إنشاء التنبيه وحفظه في قاعدة البيانات
                Notification notification = new Notification();
                notification.setTitle("Weather Alert");
                notification.setMessage(message.toString());
                notification.setType(NotificationType.INFO);
                notification.setRecipient(farm.getFarmer());
                notificationRepository.save(notification);

                // إرسال التنبيه عبر FCM إذا كان التوكن موجود
                String fcmToken = farm.getFarmer().getFcmToken();
                if (fcmToken != null && !fcmToken.isBlank()) {
                    try {
                        fcmService.sendNotificationToToken(
                            notification.getTitle(),
                            notification.getMessage(),
                            fcmToken
                        );
                    } catch (Exception e) {
                        System.err.println("❌ Failed to send FCM notification: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                // لو صار أي خطأ نطبع ونتابع
                System.err.println("❌ Error processing weather notification for farm id "
                    + farm.getId() + ": " + e.getMessage());
            }
        }
    }
}
