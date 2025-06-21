package io.farmx.enums;

public enum NotificationType {
    ORDER_ASSIGNED,
    ORDER_READY,
    PRODUCT_OUT_OF_STOCK,
    FARM_APPROVED,
    GENERAL_ANNOUNCEMENT,
    INFO,

    CROP_NEEDS_WATER,          // Irrigation needed
    CROP_READY_FOR_HARVEST,    // Harvest time!
    WEATHER_ALERT,             // Weather-related warnings (rain, frost, etc.)
    PLANTING_REMINDER,         // It's time to plant a specific crop
    FERTILIZER_REMINDER,       // Fertilizer time
    PEST_ALERT,                // Watch out for crop diseases or pests
    SOIL_CHECK_NEEDED          // Suggests soil testing if quality drops

}
