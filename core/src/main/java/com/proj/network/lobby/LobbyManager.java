package com.proj.network.lobby;


import com.badlogic.gdx.Gdx;
import com.proj.network.GameServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {
    private Map<String, GameLobby> gameLobbies = new HashMap<>();
    private GameServer server;

    public LobbyManager(GameServer server) {
        this.server = server;
    }

    public synchronized GameLobby createAndGetLobby(String lobbyName, String admin, String password, int maxPlayers, boolean isPrivate, boolean isVisible) {
        String lobbyId = generateLobbyId();
        GameLobby lobby = new GameLobby(lobbyId, lobbyName, admin, maxPlayers, isPrivate, isVisible, server);

        if (isPrivate) {
            lobby.setPassword(password);
        }

        gameLobbies.put(lobbyId, lobby);

        System.out.println("Lobby Manager : createLobby");
        System.out.println("Lobby ID : " + gameLobbies.get(lobbyId).getId());
        return lobby;
    }


    public void removePlayer(String username) {
        for (GameLobby lobby : gameLobbies.values()) {
            if (lobby.hasPlayer(username)) {
                lobby.removePlayer(username);


                if (lobby.isEmpty() && !lobby.isGameActive()) {
                    gameLobbies.remove(lobby.getId());
                    Gdx.app.log("GameServer", "lobby was deleted" + lobby.getId());
                }
            }
        }
    }

    public GameLobby findPlayerLobby(String username) {
        for (GameLobby lobby : gameLobbies.values()) {
            if (lobby.hasPlayer(username)) {
                return lobby;
            }
        }
        return null;
    }

    private String generateLobbyId() {
        return "lobby_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void checkInactiveLobbies() {
        long inactivityTimeout = 30 * 60 * 1000; // 30 minutes

        for (Map.Entry<String, GameLobby> entry : new ConcurrentHashMap<>(gameLobbies).entrySet()) {
            GameLobby lobby = entry.getValue();
            if (lobby.isEmpty() || (lobby.isInactive(inactivityTimeout) && !lobby.isGameActive())) {
                gameLobbies.remove(entry.getKey());
                Gdx.app.log("GameServer", "lobby was deleted " + entry.getKey());
            }
        }
    }

    public GameLobby getGameLobby(String lobbyId) {
        return gameLobbies.get(lobbyId);
    }

    public List<GameLobby> getGameLobbies() {
        return new ArrayList<>(gameLobbies.values());
    }

    public Map<String, GameLobby> getGameLobbiesMap() {
        return gameLobbies;
    }

    public enum GameLobbyStatus {WAITING, IN_GAME}
}
