package com.proj.network;

import com.proj.network.lobby.GameLobby;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameInstance {
    private final String gameId;
    private final GameLobby lobby;
    private final Map<String, PlayerGameState> playerStates = new HashMap<>();
    private boolean gameActive = false;
    private long lastActivityTime;
    private long lastUpdateTime;
    private static final long UPDATE_INTERVAL_MS = 1000;

    public GameInstance(String gameId, GameLobby lobby) {
        this.gameId = gameId;
        this.lobby = lobby;
        this.lastActivityTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void initialize() {
        // Create initial state for each player
        for (String username : lobby.getPlayers().keySet()) {
            PlayerGameState state = new PlayerGameState();
            state.setUsername(username);
            state.initialize();
            playerStates.put(username, state);
        }
        gameActive = true;
    }

    public boolean processAction(String username, String action, JSONObject actionData) {
        if (!gameActive) {
            return false;
        }

        PlayerGameState playerState = playerStates.get(username);
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

        // Update player states
        for (PlayerGameState playerState : playerStates.values()) {
            if (!playerState.isMoving()) {
                playerState.restoreEnergy(deltaTime);
            }
        }
    }

    public JSONObject getGameState() {
        JSONObject gameState = new JSONObject();
        gameState.put("gameId", gameId);
        gameState.put("active", gameActive);

        JSONObject playersData = new JSONObject();
        for (Map.Entry<String, PlayerGameState> entry : playerStates.entrySet()) {
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

    public PlayerGameState getPlayerState(String username) {
        return playerStates.get(username);
    }

    public boolean allAreReadyToPlay() {
        for (PlayerGameState playerState : playerStates.values()) {
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

