package com.fabioperettig.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class DatabaseConfig {

    private final Dotenv dotenv;

    private DatabaseConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public static DatabaseConfig load() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        return new DatabaseConfig(dotenv);
    }

    public ConnectionFactory createConnectionFactory() {
        return createConnectionFactory("DB");
    }

    public ConnectionFactory createTestConnectionFactory() {
        String testUrl = require("TEST_DB_URL");
        String developmentUrl = dotenv.get("DB_URL");

        if (testUrl.equals(developmentUrl)) {
            throw new IllegalStateException(
                    "TEST_DB_URL must be different from DB_URL"
            );
        }

        return new ConnectionFactory(
                testUrl,
                require("TEST_DB_USER"),
                require("TEST_DB_PASSWORD")
        );
    }

    private ConnectionFactory createConnectionFactory(String prefix) {
        return new ConnectionFactory(
                require(prefix + "_URL"),
                require(prefix + "_USER"),
                require(prefix + "_PASSWORD")
        );
    }

    private String require(String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required database configuration is missing: " + key
            );
        }

        return value;
    }
}
