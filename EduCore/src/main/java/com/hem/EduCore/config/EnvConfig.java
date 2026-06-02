package com.hem.EduCore.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    static {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
