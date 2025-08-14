package com.proj.network;

import com.proj.map.*;

public class PlayerInGame {
    private String username;
    private farmName farm;
    private boolean readyToPlay;
    private String currentMapName;
    private Position position;
    private int score = 0;
    private float lastActionTime;

    public PlayerInGame() {
        this.lastActionTime = System.currentTimeMillis();
    }

    public void initialize() {
        position = new Position(0, 0);
        score = 0;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Position getPosition() {
        return position;
    }

    public String getCurrentMapName() {
        return currentMapName;
    }

    public void setPosition(Position position, String mapName) {
        this.position = position;
        this.currentMapName = mapName;
    }


    public void setFarmName(farmName farmName) {
        this.farm = farmName;
    }

    public farmName getFarm() {
        return farm;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }
}

