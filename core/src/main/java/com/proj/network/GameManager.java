package com.proj.network;

import com.proj.network.lobby.GameLobby;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class GameManager {
    private static final Logger logger = Logger.getLogger(GameManager.class.getName());
    private final GameServer server;
    private final Map<String, GameInstance> activeGames = new ConcurrentHashMap<>();

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

        GameInstance gameInstance = new GameInstance(gameId, lobby);
        activeGames.put(gameId, gameInstance);
        lastUpdateTimes.put(gameId, System.currentTimeMillis());

        lobby.setGameActive(true);

        gameInstance.initialize();

        JSONObject initialState = gameInstance.getGameState();
        lobby.broadcastMessage("GAME_STATE", initialState.toString());
        lobby.broadcastMessage("GAME_STARTED", "game started");

        logger.info("the new game started:  " + gameId);
        return true;
    }

    public boolean processGameAction(String gameId, String username, String action, JSONObject actionData) {
        GameInstance game = activeGames.get(gameId);

        if (game == null) {
            logger.warning("بازی یافت نشد: " + gameId);
            return false;
        }

        boolean success = game.processAction(username, action, actionData);

        if (success) {
            game.updateLastActivityTime();

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTimes.getOrDefault(gameId, 0L) > UPDATE_INTERVAL_MS) {
                JSONObject newState = game.getGameState();
                game.getLobby().broadcastMessage("GAME_STATE", newState.toString());
                lastUpdateTimes.put(gameId, currentTime);
            }
        }

        return success;
    }

    public void processGameAction(GameLobby lobby, String username, String action, String actionData) {
        if (lobby == null || !lobby.isGameActive()) {
            return;
        }

        try {
            JSONObject jsonData = new JSONObject(actionData);
            processGameAction(lobby.getId(), username, action, jsonData);
        } catch (Exception e) {
            logger.warning("خطا در پردازش داده اکشن: " + e.getMessage());
        }
    }


    public void endGame(String gameId, String winner) {
        GameInstance game = activeGames.get(gameId);
        if (game == null) {
            return;
        }

        GameLobby lobby = game.getLobby();

        JSONObject endGameData = new JSONObject();
        endGameData.put("winner", winner);
        endGameData.put("gameId", gameId);
        lobby.broadcastMessage("GAME_END", endGameData.toString());

        lobby.setGameActive(false);

        try {
            server.getDatabaseHelper().saveGameResult(gameId, winner);
        } catch (Exception e) {
            logger.warning("خطا در ذخیره نتیجه بازی: " + e.getMessage());
        }

        activeGames.remove(gameId);
        lastUpdateTimes.remove(gameId);

        logger.info("بازی پایان یافت: " + gameId + "، برنده: " + winner);
    }


    public GameInstance getGameInstance(String gameId) {
        return activeGames.get(gameId);
    }


    public void checkInactiveGames() {
        long currentTime = System.currentTimeMillis();
        long inactivityTimeout = 10 * 60 * 1000; // 10 دقیقه

        for (Map.Entry<String, GameInstance> entry : new HashMap<>(activeGames).entrySet()) {
            GameInstance game = entry.getValue();

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
