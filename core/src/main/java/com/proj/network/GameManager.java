package com.proj.network;

import com.proj.network.lobby.GameLobby;
import com.proj.network.message.JsonBuilder;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class GameManager {
    private static final Logger logger = Logger.getLogger(GameManager.class.getName());
    private final GameServer server;
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    private final Map<String, Long> lastUpdateTimes = new HashMap<>();
    private static final long UPDATE_INTERVAL_MS = 100;

    public GameManager(GameServer server) {
        this.server = server;
    }

    public boolean startGame(GameLobby lobby) {
        if (lobby == null || lobby.getPlayerCount() < 2) {
            return false;
        }

        String gameId = lobby.getId();

        Game gameInstance = new Game(gameId, lobby);
        activeGames.put(gameId, gameInstance);
        lastUpdateTimes.put(gameId, System.currentTimeMillis());

        lobby.setGameActive(true);
        gameInstance.initialize();
        lobby.broadcastMessage("GAME_STARTED", "game started");

        logger.info("the new game started:  " + gameId);
        return true;
    }

    public void endGame(String gameId, String winner) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            return;
        }

        GameLobby lobby = game.getLobby();

        JSONObject endGameData = JsonBuilder.create().put("gameId", gameId).build();
        lobby.broadcastMessage("GAME_END", endGameData.toString());

        lobby.setGameActive(false);

        try {
            server.getDatabaseHelper().saveGameResult(gameId, winner);
        } catch (Exception e) {
            logger.warning("error in receiving game result:  " + e.getMessage());
        }
    }


    public Game getGame(String gameId) {
        return activeGames.get(gameId);
    }


    public void checkInactiveGames() {
        long currentTime = System.currentTimeMillis();
        long inactivityTimeout = 10 * 60 * 1000;

        for (Map.Entry<String, Game> entry : new HashMap<>(activeGames).entrySet()) {
            Game game = entry.getValue();

            if (currentTime - game.getLastActivityTime() > inactivityTimeout) {
                logger.info("inactive game ended: " + game.getGameId());
                endGame(game.getGameId(), null);
            }
        }
    }


    public int getActiveGamesCount() {
        return activeGames.size();
    }
}
