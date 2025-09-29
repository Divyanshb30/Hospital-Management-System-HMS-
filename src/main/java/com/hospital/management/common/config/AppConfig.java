package com.hospital.management.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration manager
 */
public final class AppConfig {

    private static final String CONFIG_FILE = "application.properties";
    private static final Properties properties = new Properties();
    private static AppConfig instance;

    static {
        loadProperties();
    }

    private AppConfig() {
        // Singleton pattern
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("⚠️  Configuration file not found, using default values");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            System.out.println("✅ Configuration loaded successfully");
        } catch (IOException e) {
            System.err.println("❌ Error loading configuration: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        properties.setProperty("app.name", "Hospital Management System");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.session.timeout", "30");
        properties.setProperty("app.max.login.attempts", "3");
        properties.setProperty("app.appointment.booking.advance.days", "30");
        properties.setProperty("app.consultation.fee.default", "500.00");
        properties.setProperty("app.thread.pool.size", "5");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    // Application-specific getters
    public String getApplicationName() {
        return getProperty("app.name", "Hospital Management System");
    }

    public String getApplicationVersion() {
        return getProperty("app.version", "1.0.0");
    }

    public int getSessionTimeout() {
        return getIntProperty("app.session.timeout", 30);
    }

    public int getMaxLoginAttempts() {
        return getIntProperty("app.max.login.attempts", 3);
    }

    public int getAdvanceBookingDays() {
        return getIntProperty("app.appointment.booking.advance.days", 30);
    }

    public double getDefaultConsultationFee() {
        return getDoubleProperty("app.consultation.fee.default", 500.00);
    }

    public int getThreadPoolSize() {
        return getIntProperty("app.thread.pool.size", 5);
    }
}
