package com.proj.network;

import com.proj.Model.TimeAndWeather.time.GameTime;
import com.proj.Model.TimeAndWeather.time.Time;
import com.proj.network.lobby.GameLobby;
import com.proj.network.message.JsonBuilder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInstance {
    private final String gameId;
    private final GameLobby lobby;
    private GameTime gameTimer;

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

    public List<String> getPlayers() {
        return new ArrayList<>(playerStates.keySet());
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

    public void updateTime(float deltaTime) {
        gameTimer.update(deltaTime, false);
        lobby.broadcastMessage("GAME_TIME", getTime().toString());
    }

    public JSONObject getTime() {
        JSONObject time = JsonBuilder.create().put("hour", gameTimer.getHour()).
            put("minute", gameTimer.getMinute())
            .put("day", gameTimer.getDay()).put("season", gameTimer.getSeason().toString()).
            put("weather", gameTimer.getWeather().getWeather()).
            put("dayOfWeek", gameTimer.getDayOfWeek().toString()).
            put("isNewDay", gameTimer.isNewDay()).
            build();
        return time;
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
    public List<PlayerGameState> getAllPlayers() {
        return new ArrayList<>(playerStates.values());
    }
    public boolean allAreReadyToPlay() {
        for (PlayerGameState playerState : playerStates.values()) {
            if (!playerState.isReadyToPlay()) {
                return false;
            }
        }
        gameTimer = new GameTime();
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

