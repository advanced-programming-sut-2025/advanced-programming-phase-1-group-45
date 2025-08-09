package com.proj.Database;

import com.proj.Model.User;
import java.sql.*;

import static com.proj.Control.Authenticator.isUsernameUnique;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:game_database.db";
    private Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database "+ "Connected to SQLite database at: " + System.getProperty("user.dir"));
            createUserTable();
            createGameResultsTable();
            logAllUsers();
        } catch (Exception e) {
            System.out.println("Database"+ "Connection error: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database "+ "Disconnected from database");
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Disconnection error: " + e.getMessage());
        }
    }

    public void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "username TEXT UNIQUE NOT NULL COLLATE NOCASE,"
            + "password TEXT NOT NULL,"
            + "security_answer TEXT NOT NULL)";

        executeUpdate(createTableSQL);
        System.out.println("Database " + "User table created/verified");

        addColumnIfNotExists("users", "email", "TEXT");
        addColumnIfNotExists("users", "nickname", "TEXT");
        addColumnIfNotExists("users", "gender", "TEXT");
    }

    private void addColumnIfNotExists(String table, String column, String type) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet columns = meta.getColumns(null, null, table, column)) {
                if (!columns.next()) {
                    String sql = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + type;
                    executeUpdate(sql);
                    System.out.println("Database " + "Added column " + column + " to " + table);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Error checking/adding column " + column + ": " + e.getMessage());
        }
    }

    public void executeUpdate(String query) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Database"+ "Update error: " + e.getMessage() + "\nQuery: " + query);
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Database"+ "Query error: " + e.getMessage() + "\nQuery: " + query);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public boolean addUser(String username, String password, String securityAnswer,
                           String email, String nickname, String gender) {
        System.out.println("name " + username +  " password " + password + " securityAnswer " + securityAnswer + " email " + email + " nickname " + nickname + " gender " + gender);
        String sql = "INSERT INTO users (username, password, security_answer, email, nickname, gender) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, securityAnswer);
            pstmt.setString(4, email);
            pstmt.setString(5, nickname);
            pstmt.setString(6, gender);
            pstmt.executeUpdate();

            System.out.println("Database "+ "Added user: " + username);
            return true;
        } catch (SQLException e) {
            System.out.println("Database "+ "Error adding user: " + e.getMessage());
            return false;
        }
    }

    public ResultSet getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Database"+ "Error getting user: " + e.getMessage());
            return null;
        }
    }

    public boolean verifyUser(String identifier, String password) {
        String sql = "SELECT password FROM users WHERE username = ? COLLATE NOCASE OR email = ? COLLATE NOCASE";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Verification error: " + e.getMessage());
            return false;
        }
    }

    public User authenticateUser(String identifier, String password) {
        String sql = "SELECT * FROM users WHERE (username = ? COLLATE NOCASE OR email = ? COLLATE NOCASE) AND password = ?";

        System.out.println("Database "+ "Attempting login with: " + identifier);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            pstmt.setString(3, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setSecurityAnswer(rs.getString("security_answer"));

                    user.setEmail(rs.getString("email"));
                    user.setNickname(rs.getString("nickname"));
                    user.setGender(rs.getString("gender"));

                    System.out.println("Database "+ "Authenticated user: " + user.getUsername());
                    return user;
                } else {
                    System.out.println("Database "+ "No user found for: " + identifier);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Authentication error: " + e.getMessage());
            return null;
        }
    }

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
            System.out.println("Database"+ "Security answer error: " + e.getMessage());
            return null;
        }
    }

    public void saveGameResult(String gameId, String winner) {
        String sql = "INSERT INTO game_results (game_id, winner, timestamp) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gameId);
            pstmt.setString(2, winner);
            pstmt.setLong(3, System.currentTimeMillis());
            pstmt.executeUpdate();
            System.out.println("Database "+ "Game result saved: " + gameId + ", winner: " + winner);
        } catch (SQLException e) {
            System.out.println("Database"+ "Error saving game result: " + e.getMessage());
        }
    }

    public void createGameResultsTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS game_results ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "game_id TEXT NOT NULL,"
            + "winner TEXT,"
            + "timestamp BIGINT NOT NULL)";

        executeUpdate(createTableSQL);
        System.out.println("Database "+ "Game results table created");
    }

    public void logAllUsers() {
        if (connection == null) {
            System.out.println("Database"+ "No connection to log users");
            return;
        }

        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "users", null);

            StringBuilder columnList = new StringBuilder();
            while (columns.next()) {
                String colName = columns.getString("COLUMN_NAME");
                if (columnList.length() > 0) columnList.append(", ");
                columnList.append(colName);
            }
            columns.close();

            if (columnList.length() == 0) {
                System.out.println("Database "+ "No columns found in users table");
                return;
            }

            String sql = "SELECT " + columnList.toString() + " FROM users";
            System.out.println("Database "+ "Dumping users with query: " + sql);

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Database "+ "===== USER TABLE DUMP =====");
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (String col : columnList.toString().split(", ")) {
                        if (row.length() > 0) row.append(" | ");
                        row.append(col).append(": ").append(rs.getString(col));
                    }
                    System.out.println("Database "+ row.toString());
                }
                System.out.println("Database "+ "===== END DUMP =====");
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Error dumping users: " + e.getMessage());
        }
    }

    public boolean updateUsername(User user, String newUsername) {
        // Check if username is available
        if (!isUsernameUnique(newUsername)) {
            return false;
        }

        String sql = "UPDATE users SET username = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, user.getId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Database"+ "Username update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEmail(User user, String newEmail) {
        if (!isEmailValidAndUnique(newEmail)) {
            return false;
        }

        String sql = "UPDATE users SET email = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, user.getId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Database"+ "Email update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateNickname(User user, String newNickname) {
        String sql = "UPDATE users SET nickname = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setInt(2, user.getId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Database"+ "Nickname update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(User user, String currentPassword, String newPassword) {
        if (!verifyUser(user.getUsername(), currentPassword)) {
            return false;
        }

        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, user.getId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Database"+ "Password update error: " + e.getMessage());
            return false;
        }
    }
    public boolean isEmailValidAndUnique(String email) {
        return isEmailValid(email) && isEmailUnique(email);
    }

    /**
     * Validates email format using regex
     */
    private boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    /**
     * Checks if email is unique in the database
     */
    private boolean isEmailUnique(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? COLLATE NOCASE";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Database"+ "Email uniqueness check error: " + e.getMessage());
            return false;
        }
    }
}
