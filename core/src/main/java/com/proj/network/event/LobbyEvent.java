package com.proj.network.event;

import org.json.JSONObject;

public class LobbyEvent {
    public enum Type {
        LOBBY_CREATED,
        JOINED,
        LEFT,
        LOBBIES_LIST,
        GAME_STARTED,
        UPDATED,
        ERROR;
    }

    private final Type type;
    private final String lobbyData;

    public LobbyEvent(Type type, String lobbyData) {
        this.type = type;
        this.lobbyData = lobbyData;
    }

    public Type getType() {
        return type;
    }

    public String getLobbyData() {
        return lobbyData;
    }

}
