package com.hris.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class PostgresDatabaseBootstrap {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String DATASOURCE_URL = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String POSTGRES_PREFIX = "jdbc:postgresql://";

    private PostgresDatabaseBootstrap() {
    }

    public static void ensureDatabaseExists() {
        Properties properties = loadProperties();
        String datasourceUrl = properties.getProperty(DATASOURCE_URL);

        if (datasourceUrl == null || !datasourceUrl.startsWith(POSTGRES_PREFIX)) {
            return;
        }

        String databaseName = extractDatabaseName(datasourceUrl);
        String adminUrl = buildAdminUrl(datasourceUrl);
        String username = properties.getProperty(DATASOURCE_USERNAME);
        String password = properties.getProperty(DATASOURCE_PASSWORD);

        try (Connection connection = DriverManager.getConnection(adminUrl, username, password)) {
            if (databaseExists(connection, databaseName)) {
                return;
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE DATABASE \"" + databaseName.replace("\"", "\"\"") + "\"");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to ensure PostgreSQL database exists: " + databaseName, e);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = PostgresDatabaseBootstrap.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing " + PROPERTIES_FILE);
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + PROPERTIES_FILE, e);
        }
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, databaseName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static String extractDatabaseName(String datasourceUrl) {
        String urlWithoutPrefix = datasourceUrl.substring(POSTGRES_PREFIX.length());
        int slashIndex = urlWithoutPrefix.indexOf('/');
        if (slashIndex < 0 || slashIndex == urlWithoutPrefix.length() - 1) {
            throw new IllegalStateException("Invalid PostgreSQL datasource URL: " + datasourceUrl);
        }

        String databasePart = urlWithoutPrefix.substring(slashIndex + 1);
        int queryIndex = databasePart.indexOf('?');
        return queryIndex >= 0 ? databasePart.substring(0, queryIndex) : databasePart;
    }

    private static String buildAdminUrl(String datasourceUrl) {
        int lastSlashIndex = datasourceUrl.lastIndexOf('/');
        if (lastSlashIndex < 0) {
            throw new IllegalStateException("Invalid PostgreSQL datasource URL: " + datasourceUrl);
        }

        int queryIndex = datasourceUrl.indexOf('?', lastSlashIndex);
        String suffix = queryIndex >= 0 ? datasourceUrl.substring(queryIndex) : "";
        return datasourceUrl.substring(0, lastSlashIndex + 1) + "postgres" + suffix;
    }
}
