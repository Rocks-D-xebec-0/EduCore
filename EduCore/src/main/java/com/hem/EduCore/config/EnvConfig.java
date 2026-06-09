package com.hem.EduCore.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            System.out.println("Warning: Could not load .env file. Using default/system properties.");
        }
    }
}
