package com.teachmanage.server.core;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private final Properties properties = new Properties();

    public static DbConfig load() throws Exception {
        DbConfig config = new DbConfig();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                config.properties.load(in);
            }
        }
        return config;
    }

    public int serverPort() {
        return Integer.parseInt(value("server.port", "TMS_SERVER_PORT", "8080"));
    }

    public String dbUrl() {
        return value("db.url", "TMS_DB_URL", "");
    }

    public String dbUsername() {
        return value("db.username", "TMS_DB_USERNAME", "root");
    }

    public String dbPassword() {
        return value("db.password", "TMS_DB_PASSWORD", "");
    }

    public boolean initSchema() {
        return Boolean.parseBoolean(value("db.init-schema", "TMS_DB_INIT_SCHEMA", "false"));
    }

    private String value(String key, String envKey, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return properties.getProperty(key, defaultValue);
    }
}
