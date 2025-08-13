package com.proj.network.lobby;

import com.proj.network.ClientConnectionController;
import com.proj.network.Game;
import com.proj.network.Server;
import com.proj.network.PlayerInGame;
import com.proj.network.message.JsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameLobby {
    private String id;
    private String name;
    private String admin;
    private int maxPlayers;
    private boolean isPrivate;
    private boolean isVisible;
    private String password;
    private boolean gameActive = false;
    private long creationTime;
    private long lastActivityTime;

    private final Map<String, ClientConnectionController> players = new ConcurrentHashMap<>();
    private final Map<String, PlayerInGame> playerStates = new ConcurrentHashMap<>();

    private Game game;
    private Server server;

    public GameLobby() {
    }

    public GameLobby(String id, String name, String admin, int maxPlayers, boolean isPrivate, boolean isVisible, Server server) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.server = server;
        this.creationTime = System.currentTimeMillis();
        this.lastActivityTime = System.currentTimeMillis();
    }

    public void addPlayer(String username, ClientConnectionController handler) {
        players.put(username, handler);
        handler.setCurrentLobby(this);
        updateLastActivity();

        // اگر صاحب لابی خارج شده بود، اولین بازیکن جدید صاحب لابی می‌شود
        if (admin == null && !players.isEmpty()) {
            admin = username;
            broadcastMessage("SYSTEM", username + " is the new lobby admin");
        }

        if (gameActive && game != null) {
            PlayerInGame state = new PlayerInGame();
            state.setUsername(username);
            state.initialize();
            playerStates.put(username, state);

            handler.sendMessage("GAME_STATE", game.getGameState());
        }

        broadcastMessage("LOBBY_UPDATE", getLobbyInfo().toString());
        server.notifyPlayerStatusUpdate();
    }

    public void sendPlayerPositions() {
        JSONArray players = new JSONArray();
        Game game = server.getGameManager().getGameInstance(id);
        if (game != null) {
            for (PlayerInGame playerInGame : game.getAllPlayers()) {
                JSONObject position = new JSONObject();
                position.put("username", playerInGame.getUsername()).put("x", playerInGame.getPosition().getX()).put("y",
                    playerInGame.getPosition().getY()).put("mapName",
                    playerInGame.getCurrentMapName());
                players.put(position);
            }

            JSONObject allPlayers = new JSONObject();
            allPlayers.put("players", players);
            broadcastMessage("PLAYER_POSITIONS", allPlayers.toString());
            System.out.println("GAmeLobby  sending position ");
        } else {
            System.out.println("GAmeLobby  sending position error");
        }
    }

    /**
     * حذف بازیکن از لابی
     */
    public void removePlayer(String username) {
        players.remove(username);
        playerStates.remove(username);
        updateLastActivity();

        // اگر صاحب لابی خارج شد و بازیکن دیگری هست
        if (username.equals(admin) && !players.isEmpty()) {
            // انتخاب اولین بازیکن به عنوان صاحب جدید
            admin = players.keySet().iterator().next();
            broadcastMessage("SYSTEM", admin + " is the new lobby admin");
        }

        // اگر بازی در حال اجراست، بررسی کنیم آیا باید بازی را متوقف کنیم
        if (gameActive && game != null && players.size() < 2) {
            endGame("players are not enough to continue game");
        }

        if (!players.isEmpty()) {
            broadcastMessage("LOBBY_UPDATE", getLobbyInfo().toString());
        }

        server.notifyPlayerStatusUpdate();
//        server.broadcastLobbiesList();
    }

    /**
     * ارسال پیام به تمام بازیکنان لابی
     */
    public void broadcastMessage(String type, String message) {
        for (ClientConnectionController client : players.values()) {
            client.sendMessage(type, JsonBuilder.create().put("data", message).build());
        }
    }

    public void broadcastRaw(String message) {
        for (ClientConnectionController handler : players.values()) {
            if (!handler.isTimedOut()) {
                handler.sendRaw(message);
            }
        }
    }

    public void endGame(String reason) {
        if (!gameActive) {
            return;
        }

        gameActive = false;

        JSONObject endData = new JSONObject();
        endData.put("reason", reason);
        broadcastMessage("GAME_END", endData.toString());

        playerStates.clear();
        game = null;
    }


    public JSONObject getLobbyInfo() {
        return new JsonBuilder()
            .put("id", id)
            .put("name", name)
            .put("admin", admin)
            .put("playerCount", getPlayerCount())
            .put("maxPlayers", maxPlayers)
            .put("isPrivate", isPrivate)
            .put("isVisible", isVisible)
            .put("isGameActive", isGameActive())
            .put("players", new JSONArray(players.keySet()))
            .build();
    }

    public boolean checkPassword(String password) {
        if (!isPrivate) {
            return true;
        }
        return this.password != null && this.password.equals(password);
    }

    public void updateLastActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    public boolean isInactive(long timeoutMs) {
        return System.currentTimeMillis() - lastActivityTime > timeoutMs;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean active) {
        this.gameActive = active;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public Map<String, ClientConnectionController> getPlayers() {
        return players;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPlayer(String username) {
        return players.containsKey(username);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public PlayerInGame getPlayerState(String username) {
        return playerStates.get(username);
    }

    public Game getGameInstance() {
        return game;
    }

    public Server getGameServer() {
        return server;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getAdminId() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setGameInstance(Game game) {
        this.game = game;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setName(String name) {
        this.name = name;
    }
}
