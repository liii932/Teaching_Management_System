package com.teachmanage.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbManager {
    private static final Logger log = LoggerFactory.getLogger(DbManager.class);

    private final DbConfig config;

    public DbManager(DbConfig config) {
        this.config = config;
    }

    public Connection open() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(config.dbUrl(), config.dbUsername(), config.dbPassword());
        conn.setAutoCommit(true);
        if (config.initSchema()) {
            executeSchema(conn);
        }
        return conn;
    }

    private void executeSchema(Connection conn) throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("db/edusystem.sql");
        if (in == null) {
            log.warn("db/edusystem.sql not found, skip schema initialization");
            return;
        }
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                sql.append(line).append('\n');
            }
        }
        try (Statement stmt = conn.createStatement()) {
            for (String part : sql.toString().split(";")) {
                String statement = part.trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
        log.info("Database schema initialized");
    }
}
