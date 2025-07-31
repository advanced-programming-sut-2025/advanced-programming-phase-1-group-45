package com.proj.Database;

import com.badlogic.gdx.Gdx;
import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:game_database.db";
    private Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            Gdx.app.log("Database", "Connected to SQLite database");
            createUserTable();// add our table here
        } catch (Exception e) {
            Gdx.app.error("Database", "Connection error: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Gdx.app.log("Database", "Disconnected from database");
            }
        } catch (SQLException e) {
            Gdx.app.error("Database", "Disconnection error: " + e.getMessage());
        }
    }

    // we will add our tables here like this:
    public void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "username TEXT UNIQUE NOT NULL COLLATE NOCASE,"
            + "password TEXT NOT NULL,"
            + "security_answer TEXT NOT NULL)";

        executeUpdate(createTableSQL);
        Gdx.app.log("Database", "User table created");
    }

    public void executeUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            Gdx.app.error("Database", "Update error: " + e.getMessage() + "\nQuery: " + query);
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            Gdx.app.error("Database", "Query error: " + e.getMessage() + "\nQuery: " + query);
            return null;
        }
    }

    // Helper for prepared statements
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    //all these methods for checking signup part:
    // Add a new user
    public boolean addUser(String username, String password, String securityQuestion) {
        String sql = "INSERT INTO users (username, password, security_question) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, securityQuestion);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Gdx.app.error("Database", "Error adding user: " + e.getMessage());
            return false;
        }
    }

    // Get a user by username
    public ResultSet getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            Gdx.app.error("Database", "Error getting user: " + e.getMessage());
            return null;
        }
    }

    // Verify user credentials
    public boolean verifyUser(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a match was found
        } catch (SQLException e) {
            Gdx.app.error("Database", "Verification error: " + e.getMessage());
            return false;
        }
    }

    // Get security question for a user
    public String getSecurityAnswer(String username) {
        String sql = "SELECT security_answer FROM users WHERE username = ? COLLATE NOCASE";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("security_answer");
                }
                return null;
            }
        } catch (SQLException e) {
            Gdx.app.error("Database", "Security answer error: " + e.getMessage());
            return null;
        }
    }
}
