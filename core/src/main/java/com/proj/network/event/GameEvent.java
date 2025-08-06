package com.proj.network.event;

public class GameEvent {
    public enum Type {
        STARTED,
        UPDATE,
        ENDED,
        ACTION,
        PLAYER_MOVED,
    }

    private final Type type;
    private final String gameData;

    public GameEvent(Type type, String gameData) {
        this.type = type;
        this.gameData = gameData;
    }

    public Type getType() {
        return type;
    }

    public String getGameData() {
        return gameData;
    }
}
