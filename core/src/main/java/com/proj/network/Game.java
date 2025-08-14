package com.proj.network;

import com.proj.Model.TimeAndWeather.Weather;
import com.proj.Model.TimeAndWeather.time.GameTime;
import com.proj.network.lobby.GameLobby;
import com.proj.network.message.JsonBuilder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final String gameId;
    private final GameLobby lobby;
    private GameTime gameTimer;

    private final Map<String, PlayerInGame> playerStates = new HashMap<>();
    private boolean gameActive = false;
    private long lastActivityTime;
    private long lastUpdateTime;

    public Game(String gameId, GameLobby lobby) {
        this.gameId = gameId;
        this.lobby = lobby;
        this.lastActivityTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public List<String> getPlayers() {
        return new ArrayList<>(playerStates.keySet());
    }

    public void initialize() {
        for (String username : lobby.getPlayers().keySet()) {
            PlayerInGame state = new PlayerInGame();
            state.setUsername(username);
            state.initialize();
            playerStates.put(username, state);
        }
        gameActive = true;
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

    public PlayerInGame getPlayerState(String username) {
        return playerStates.get(username);
    }
    public List<PlayerInGame> getAllPlayers() {
        return new ArrayList<>(playerStates.values());
    }
    public boolean allAreReadyToPlay() {
        for (PlayerInGame playerState : playerStates.values()) {
            if (!playerState.isReadyToPlay()) {
                return false;
            }
        }
        gameTimer = new GameTime();
        return true;
    }

    public void changeWeather(Weather weather) {
        gameTimer.setWeather(weather);
        lobby.broadcastMessage("GAME_TIME", getTime().toString());
    }

    public void changeTime(int hour) {
        gameTimer.advanceHour(hour);
        lobby.broadcastMessage("GAME_TIME", getTime().toString());
    }

    public void changeDay(int day) {
        gameTimer.advanceDay(day);
        lobby.broadcastMessage("GAME_TIME", getTime().toString());
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

