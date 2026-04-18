package com.expensetracker.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {
    private static final Map<String, String> env = new HashMap<>();

    static {
        loadEnv();
    }

    private static void loadEnv() {
        String envFilePath = Paths.get(".env").toAbsolutePath().toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    env.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: .env file not found. Using default values if available.");
        }
    }

    public static String get(String key, String defaultValue) {
        return env.getOrDefault(key, defaultValue);
    }

    public static String get(String key) {
        return env.get(key);
    }
}
