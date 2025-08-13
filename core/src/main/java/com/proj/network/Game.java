package com.proj.network;

import com.proj.network.lobby.GameLobby;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private Server server;
    private final String gameId;
    private final GameLobby lobby;
    private final Map<String, PlayerInGame> players = new HashMap<>();
    private boolean gameActive = false;
    private long lastActivityTime;
    private long lastUpdateTime;
    private static final long UPDATE_INTERVAL_MS = 1000;

    public Game(String gameId, GameLobby lobby) {
        this.gameId = gameId;
        this.lobby = lobby;
        this.lastActivityTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
        server = lobby.getGameServer();
    }

    public List<String> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    public void initialize() {
        for (String username : lobby.getPlayers().keySet()) {
            PlayerInGame player = new PlayerInGame();
            player.setUsername(username);
            player.initialize();
            players.put(username, player);
        }
        gameActive = true;
    }

    public boolean processAction(String username, String action, JSONObject actionData) {
        if (!gameActive) {
            return false;
        }

        PlayerInGame playerState = players.get(username);
        if (playerState == null) {
            return false;
        }

        updateLastActivityTime();
        return true;
    }

    public void update(float deltaTime) {
        if (!gameActive) {
            return;
        }

        // Update player players
//        for (PlayerInGame playerState : players.values()) {
//            if (!playerState.isMoving()) {
//                playerState.restoreEnergy(deltaTime);
//            }
//        }
    }

    public JSONObject getGameState() {
        JSONObject gameState = new JSONObject();
        gameState.put("gameId", gameId);
        gameState.put("active", gameActive);

        JSONObject playersData = new JSONObject();
        for (Map.Entry<String, PlayerInGame> entry : players.entrySet()) {
            playersData.put(entry.getKey(), entry.getValue().toJSON());
        }
        gameState.put("players", playersData);

        return gameState;
    }

    public boolean shouldSendUpdate() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL_MS) {
            lastUpdateTime = currentTime;
            return true;
        }
        return false;
    }

    public PlayerInGame getPlayerState(String username) {
        return players.get(username);
    }
    public List<PlayerInGame> getAllPlayers() {
        return new ArrayList<>(players.values());
    }
    public boolean allAreReadyToPlay() {
        for (PlayerInGame playerState : players.values()) {
            if (!playerState.isReadyToPlay()) {
                return false;
            }
        }
        return true;
    }

    public void updateLastActivityTime() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public String getGameId() {
        return gameId;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public boolean isGameActive() {
        return gameActive;
    }
}

