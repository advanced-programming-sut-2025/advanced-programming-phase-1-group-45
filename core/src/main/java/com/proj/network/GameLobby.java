package com.proj.network;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameLobby {
    private final String id;
    private final String name;
    private String owner;
    private final int maxPlayers;
    private final boolean isPrivate;
    private final boolean isVisible;
    private String password;
    private boolean gameActive = false;
    private long creationTime;
    private long lastActivityTime;

    // نگهداری اتصال‌های بازیکنان
    private final Map<String, ClientHandler> players = new ConcurrentHashMap<>();
    // نگهداری وضعیت بازیکنان در بازی
    private final Map<String, PlayerGameState> playerStates = new ConcurrentHashMap<>();

    // مدیریت بازی
    private GameInstance gameInstance;
    private final GameServer server;

    public GameLobby(String id, String name, String owner, int maxPlayers, boolean isPrivate, boolean isVisible, GameServer server) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.server = server;
        this.creationTime = System.currentTimeMillis();
        this.lastActivityTime = System.currentTimeMillis();
    }

    /**
     * افزودن بازیکن به لابی
     */
    public void addPlayer(String username, ClientHandler handler) {
        players.put(username, handler);
        updateLastActivity();

        // اگر صاحب لابی خارج شده بود، اولین بازیکن جدید صاحب لابی می‌شود
        if (owner == null && !players.isEmpty()) {
            owner = username;
            broadcastMessage("SYSTEM", username + " صاحب جدید لابی شد");
        }

        // ایجاد وضعیت بازی برای بازیکن جدید اگر بازی در حال اجراست
        if (gameActive && gameInstance != null) {
            PlayerGameState state = new PlayerGameState();
            state.setUsername(username);
            state.initialize();
            playerStates.put(username, state);

            // ارسال وضعیت فعلی بازی به بازیکن جدید
            handler.sendMessage("GAME_STATE", gameInstance.getGameState().toString());
        }

        // اطلاع‌رسانی به همه بازیکنان
        broadcastMessage("LOBBY_UPDATE", getLobbyInfo().toString());
    }

    /**
     * حذف بازیکن از لابی
     */
    public void removePlayer(String username) {
        players.remove(username);
        playerStates.remove(username);
        updateLastActivity();

        // اگر صاحب لابی خارج شد و بازیکن دیگری هست
        if (username.equals(owner) && !players.isEmpty()) {
            // انتخاب اولین بازیکن به عنوان صاحب جدید
            owner = players.keySet().iterator().next();
            broadcastMessage("SYSTEM", owner + " صاحب جدید لابی شد");
        }

        // اگر بازی در حال اجراست، بررسی کنیم آیا باید بازی را متوقف کنیم
        if (gameActive && gameInstance != null && players.size() < 2) {
            endGame("تعداد بازیکنان کافی نیست");
        }

        // اطلاع‌رسانی به بازیکنان باقی‌مانده
        if (!players.isEmpty()) {
            broadcastMessage("LOBBY_UPDATE", getLobbyInfo().toString());
        }
    }

    /**
     * ارسال پیام به تمام بازیکنان لابی
     */
    public void broadcastMessage(String type, String message) {
        for (ClientHandler handler : players.values()) {
            handler.sendMessage(type, message);
        }
    }

    /**
     * شروع بازی
     */
    public boolean startGame() {
        if (gameActive) {
            return false; // بازی قبلاً شروع شده است
        }

        if (players.size() < 2) {
            return false; // تعداد بازیکنان کافی نیست
        }

        gameActive = true;
        gameInstance = new GameInstance(id, this);
        gameInstance.initialize();

        // ایجاد وضعیت اولیه برای هر بازیکن
        for (String username : players.keySet()) {
            PlayerGameState state = new PlayerGameState();
            state.setUsername(username);
            state.initialize();
            playerStates.put(username, state);
        }

        // ارسال وضعیت بازی به همه بازیکنان
        broadcastMessage("GAME_STATE", gameInstance.getGameState().toString());
        broadcastMessage("GAME_STARTED", "بازی شروع شد");

        return true;
    }

    /**
     * پایان بازی
     */
    public void endGame(String reason) {
        if (!gameActive) {
            return;
        }

        gameActive = false;

        // ارسال پیام پایان بازی به همه بازیکنان
        JSONObject endData = new JSONObject();
        endData.put("reason", reason);
        broadcastMessage("GAME_END", endData.toString());

        // پاک کردن وضعیت بازی
        playerStates.clear();
        gameInstance = null;
    }

    /**
     * به‌روزرسانی وضعیت بازیکن
     */
    public void updatePlayerState(String username, PlayerGameState state) {
        playerStates.put(username, state);
        updateLastActivity();
    }

    /**
     * دریافت اطلاعات لابی به صورت JSON
     */
    public JSONObject getLobbyInfo() {
        JSONObject info = new JSONObject();
        info.put("id", id);
        info.put("name", name);
        info.put("owner", owner);
        info.put("maxPlayers", maxPlayers);
        info.put("playerCount", players.size());
        info.put("isPrivate", isPrivate);
        info.put("isVisible", isVisible);
        info.put("isGameActive", gameActive);

        // اضافه کردن لیست بازیکنان
        JSONObject playersObj = new JSONObject();
        for (String playerName : players.keySet()) {
            playersObj.put(playerName, true);
        }
        info.put("players", playersObj);

        return info;
    }

    /**
     * بررسی اعتبار رمز عبور
     */
    public boolean checkPassword(String password) {
        if (!isPrivate) {
            return true;
        }
        return this.password != null && this.password.equals(password);
    }

    /**
     * به‌روزرسانی زمان آخرین فعالیت
     */
    public void updateLastActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    /**
     * بررسی عدم فعالیت طولانی
     */
    public boolean isInactive(long timeoutMs) {
        return System.currentTimeMillis() - lastActivityTime > timeoutMs;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public int getMaxPlayers() { return maxPlayers; }
    public boolean isPrivate() { return isPrivate; }
    public boolean isVisible() { return isVisible; }
    public boolean isGameActive() { return gameActive; }
    public void setGameActive(boolean active) { this.gameActive = active; }
    public int getPlayerCount() { return players.size(); }
    public Map<String, ClientHandler> getPlayers() { return players; }
    public void setPassword(String password) { this.password = password; }
    public boolean hasPlayer(String username) { return players.containsKey(username); }
    public boolean isEmpty() { return players.isEmpty(); }
    public boolean isFull() { return players.size() >= maxPlayers; }
    public PlayerGameState getPlayerState(String username) { return playerStates.get(username); }
    public GameInstance getGameInstance() { return gameInstance; }
    public GameServer getGameServer() { return server; }
    public long getCreationTime() { return creationTime; }
}
