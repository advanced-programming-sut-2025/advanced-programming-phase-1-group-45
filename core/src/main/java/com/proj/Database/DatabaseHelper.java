package com.proj.Database;

import com.proj.Model.User;
import java.sql.*;
import com.proj.Model.ScoreboardEntry;
import java.util.*;

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
            createCharacterTable();
            migrateDatabase();
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
      public void createCharacterTable() {

        String createTableSQL = "CREATE TABLE IF NOT EXISTS characters ("

            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"

            + "user_id INTEGER NOT NULL UNIQUE,"

            + "character_path TEXT NOT NULL,"

            + "animal_path TEXT NOT NULL,"

            + "farm_name TEXT NOT NULL,"

            + "farm_type TEXT NOT NULL,"

            + "money INTEGER DEFAULT 1000,"

            + "FOREIGN KEY(user_id) REFERENCES users(id))";

        executeUpdate(createTableSQL);
        System.out.println("Database " + "Character table created/verified");

    }

    public boolean updatePlayerMoney(int userId, int money) {

        String sql = "UPDATE characters SET money = ? WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, money);

            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {

            System.out.println("Database"+ "Error updating money: " + e.getMessage());

            return false;

        }

    }

    public List<ScoreboardEntry> getScoreboardData() {

        List<ScoreboardEntry> entries = new ArrayList<>();

        // Hardcoded list of the 40 specified usernames

        List<String> usernames = Arrays.asList(

            "fati", "arm", "at", "ta", "ty", "yt", "name", "nam", "us", "me",

            "job", "use", "ak", "ka", "ee", "er", "ga", "ha", "PR", "JJ",

            "s2", "s1", "s3", "KK", "PK", "FB", "KV", "JH", "MN", "FQ",

            "RTU", "fatemeB", "sara", "OOO", "arm2", "mi2", "", "HH", "H", "aa"

        );



        // Create placeholders for SQL IN clause

        String placeholders = String.join(",", Collections.nCopies(usernames.size(), "?"));

        String sql = "SELECT u.username, COALESCE(c.money, 0) AS money, " +

            "COALESCE(c.quests_completed, 0) AS quests, " +

            "COALESCE(c.skill_level, 1) AS skill_level " +

            "FROM users u " +

            "LEFT JOIN characters c ON u.id = c.user_id " +

            "WHERE u.username IN (" + placeholders + ") " +

            "ORDER BY money DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set usernames for the IN clause
            for (int i = 0; i < usernames.size(); i++) {
                pstmt.setString(i + 1, usernames.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    int money = rs.getInt("money");
                    int quests = rs.getInt("quests");
                    int skillLevel = rs.getInt("skill_level");
                    entries.add(new ScoreboardEntry(username, money, quests, skillLevel));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error getting scoreboard data: " + e.getMessage());
        }
        return entries;
    }

    public boolean saveCharacter(int userId, String characterPath, String animalPath, String farmName, String farmType, int money) {
        String sql = "INSERT OR REPLACE INTO characters (user_id, character_path, animal_path, farm_name, farm_type, money) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, characterPath);
            pstmt.setString(3, animalPath);
            pstmt.setString(4, farmName);
            pstmt.setString(5, farmType);
            pstmt.setInt(6, 1000); // ADD MONEY VALUE

            pstmt.executeUpdate();
            System.out.println("Database "+ "Character saved for user: " + userId);
            return true;
        } catch (SQLException e) {
            System.out.println("Database"+ "Error saving character: " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> loadCharacter(int userId) {
        String sql = "SELECT * FROM characters WHERE user_id = ?";
        Map<String, String> characterData = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                characterData.put("character_path", rs.getString("character_path"));
                characterData.put("animal_path", rs.getString("animal_path"));
                characterData.put("farm_name", rs.getString("farm_name"));
                characterData.put("farm_type", rs.getString("farm_type"));
            }
        } catch (SQLException e) {
            System.out.println("Database"+ "Error loading character: " + e.getMessage());
        }
        return characterData;
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

     public void migrateDatabase() {
        // Add money column if it doesn't exist
        addColumnIfNotExists("characters", "money", "INTEGER DEFAULT 1000");
        // Set default money for existing players
        String updateSql = "UPDATE characters SET money = 1000 WHERE money IS NULL";
        executeUpdate(updateSql);
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

    private boolean columnExists(String table, String column) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, table, column)) {
            return rs.next();
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
