package com.flightreservation.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DatabaseManager handles SQLite database connectivity and initialization.
 * Follows the Singleton pattern for connection lifecycle management.
 * Stereotype: <<control>> / Data Layer Manager
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private String dbUrl;

    private DatabaseManager() {
        loadConfig();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void loadConfig() {
        this.dbUrl = "jdbc:sqlite:flight_reservation.db";
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found on classpath: " + e.getMessage());
        }
        try (InputStream in = getClass().getResourceAsStream("/application.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                this.dbUrl = props.getProperty("db.url", this.dbUrl);
            }
        } catch (Exception e) {
            System.err.println("Could not load application.properties, using default: " + dbUrl);
        }
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public synchronized void initializeDatabase() {
        try (Connection conn = getConnection()) {
            executeSqlScript(conn, "/schema.sql");
            executeSqlScript(conn, "/seed-data.sql");
            System.out.println("Database initialized successfully at: " + dbUrl);
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeSqlScript(Connection conn, String scriptPath) {
        try (InputStream in = getClass().getResourceAsStream(scriptPath)) {
            if (in == null) {
                System.err.println("Script not found on classpath: " + scriptPath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Strip inline comments
                    int commentIdx = line.indexOf("--");
                    if (commentIdx >= 0) {
                        line = line.substring(0, commentIdx);
                    }
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    sql.append(trimmed).append(" ");
                    if (trimmed.endsWith(";")) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql.toString());
                        } catch (SQLException e) {
                            if (!e.getMessage().contains("already exists") && !e.getMessage().contains("UNIQUE constraint")) {
                                System.err.println("SQL execution notice (" + scriptPath + "): " + e.getMessage());
                            }
                        }
                        sql.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading SQL script " + scriptPath + ": " + e.getMessage());
        }
    }
}
