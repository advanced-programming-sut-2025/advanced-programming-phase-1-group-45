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
                    System.err.println("GameServer" + "lobby was deleted" + lobby.getId());
                }
            }
        }
    }


    private String generateLobbyId() {
        return "lobby_" + UUID.randomUUID().toString().substring(0, 8);
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
