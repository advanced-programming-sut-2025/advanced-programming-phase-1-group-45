package com.proj.network;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.proj.map.*;

public class PlayerGameState {
    private String username;
    private farmName farm;
    private boolean readyToPlay;
    private Position position;
    private boolean isMoving = false;
    private PlayerDirection direction = PlayerDirection.DOWN;
    private int energy = 100;
    private int score = 0;
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Integer> skills = new HashMap<>();
    private long lastActionTime;

    public PlayerGameState() {
        this.lastActionTime = System.currentTimeMillis();
    }

    public void initialize() {
        position = new Position(0, 0);
        energy = 100;
        score = 0;
        direction = PlayerDirection.DOWN;
        isMoving = false;

        inventory.put("coin", 100);
//        inventory.put("health_potion", 2);
//        inventory.put("energy_potion", 2);

        skills.put("farming", 1);
        skills.put("mining", 1);
        skills.put("combat", 1);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("position", new JSONObject()
            .put("x", position.getX())
            .put("y", position.getY()));
        json.put("direction", direction.toString());
        json.put("isMoving", isMoving);
        json.put("energy", energy);
        json.put("score", score);

        JSONObject inventoryJson = new JSONObject();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            inventoryJson.put(entry.getKey(), entry.getValue());
        }
        json.put("inventory", inventoryJson);

        JSONObject skillsJson = new JSONObject();
        for (Map.Entry<String, Integer> entry : skills.entrySet()) {
            skillsJson.put(entry.getKey(), entry.getValue());
        }
        json.put("skills", skillsJson);

        return json;
    }

    public boolean hasItem(String itemId) {
        return inventory.getOrDefault(itemId, 0) > 0;
    }

    public void removeItem(String itemId) {
        if (hasItem(itemId)) {
            inventory.put(itemId, inventory.get(itemId) - 1);
            if (inventory.get(itemId) <= 0) {
                inventory.remove(itemId);
            }
        }
    }

    public void restoreEnergy(float amount) {
        energy = Math.min(100, energy + (int)amount);
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { isMoving = moving; }

    public PlayerDirection getDirection() { return direction; }
    public void setDirection(PlayerDirection direction) { this.direction = direction; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }


    public void setFarmName(farmName farmName) {
        this.farm = farmName;
    }
    public farmName getFarm() {
        return farm;
    }

    public void setReadyToPlay(boolean readyToPlay) { this.readyToPlay = readyToPlay; }
    public boolean isReadyToPlay() { return readyToPlay; }
}

