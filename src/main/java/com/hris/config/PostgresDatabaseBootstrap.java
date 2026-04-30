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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PostgresDatabaseBootstrap {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String PROFILED_PROPERTIES = "application-%s.properties";
    private static final String ACTIVE_PROFILE = "spring.profiles.active";
    private static final String DATASOURCE_URL = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String POSTGRES_PREFIX = "jdbc:postgresql://";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");

    private PostgresDatabaseBootstrap() {
    }

    public static void ensureDatabaseExists() {
        Properties properties = loadProperties();
        String datasourceUrl = resolveProperty(DATASOURCE_URL, properties);

        if (datasourceUrl == null || !datasourceUrl.startsWith(POSTGRES_PREFIX)) {
            return;
        }

        String databaseName = extractDatabaseName(datasourceUrl);
        String adminUrl = buildAdminUrl(datasourceUrl);
        String username = resolveProperty(DATASOURCE_USERNAME, properties);
        String password = resolveProperty(DATASOURCE_PASSWORD, properties);

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

        try {
            loadInto(properties, PROPERTIES_FILE);

            String activeProfile = resolveValue(properties.getProperty(ACTIVE_PROFILE));
            if (activeProfile != null && !activeProfile.isBlank()) {
                for (String profile : activeProfile.split(",")) {
                    String trimmedProfile = profile.trim();
                    if (!trimmedProfile.isEmpty()) {
                        loadInto(properties, PROFILED_PROPERTIES.formatted(trimmedProfile));
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration properties", e);
        }

        return properties;
    }

    private static void loadInto(Properties properties, String fileName) throws IOException {
        try (InputStream inputStream = PostgresDatabaseBootstrap.class.getClassLoader()
                .getResourceAsStream(fileName)) {
            if (inputStream == null) {
                return;
            }
            properties.load(inputStream);
        }
    }

    private static String resolveProperty(String key, Properties properties) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return resolveValue(properties.getProperty(key));
    }

    private static String resolveValue(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return value;
        }

        String variable = matcher.group(1);
        String defaultValue = matcher.group(2);

        String resolved = System.getProperty(variable);
        if (resolved == null || resolved.isBlank()) {
            resolved = System.getenv(variable);
        }
        if ((resolved == null || resolved.isBlank()) && variable.contains(".")) {
            String envStyleVariable = variable.toUpperCase().replace('.', '_').replace('-', '_');
            resolved = System.getenv(envStyleVariable);
        }

        if (resolved == null || resolved.isBlank()) {
            return defaultValue;
        }

        return resolved;
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
