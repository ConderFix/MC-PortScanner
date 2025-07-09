package ru.quizie;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    public static void init() {
        try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (Throwable throwable) {
            System.out.println("Error on init config: " + throwable);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
