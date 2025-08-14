package com.proj.network;

import com.proj.network.lobby.GameLobby;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameUpdateTask implements Runnable {
    private final GameServer server;
    private static final int UPDATE_RATE = 60;
    private static final long FRAME_TIME = 1000 / UPDATE_RATE;
    private boolean running = true;

    public GameUpdateTask(GameServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        long lastUpdateTime = System.currentTimeMillis();

        while (running) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;

            if (deltaTime >= FRAME_TIME) {
                float deltaSeconds = deltaTime / 1000f;
                lastUpdateTime = currentTime;

                try {
                    updateGames(deltaSeconds);
                    checkDisconnectedClients();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateGames(float deltaTime) {
        for (GameLobby lobby : server.getGameLobbies().values()) {
            if (lobby.isGameActive()) {
                Game game = server.getGameManager().getGameInstance(lobby.getId());
                if (game != null) {
                    game.update(deltaTime);
                }
            }
        }
    }

    private void checkDisconnectedClients() {
    }

    public void stop() {
        running = false;
    }
}
