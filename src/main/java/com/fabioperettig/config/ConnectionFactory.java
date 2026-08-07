package com.fabioperettig.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory {

    private final String url;
    private final String user;
    private final String password;

    public ConnectionFactory(String url, String user, String password) {
        this.url = Objects.requireNonNull(url, "Database URL is required");
        this.user = Objects.requireNonNull(user, "Database user is required");
        this.password = Objects.requireNonNull(password, "Database password is required");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
