package com.proj.network;

import com.proj.network.lobby.GameLobby;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * کلاس وظیفه به‌روزرسانی بازی
 */
public class GameUpdateTask implements Runnable {
    private static final Logger logger = Logger.getLogger(GameUpdateTask.class.getName());
    private final GameServer server;
    private static final int UPDATE_RATE = 60; // تعداد به‌روزرسانی در ثانیه
    private static final long FRAME_TIME = 1000 / UPDATE_RATE; // زمان هر فریم به میلی‌ثانیه
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
                // محاسبه زمان گذشته به ثانیه
                float deltaSeconds = deltaTime / 1000f;
                lastUpdateTime = currentTime;

                try {
                    // به‌روزرسانی تمام بازی‌های فعال
                    updateGames(deltaSeconds);

                    // به‌روزرسانی وضعیت بازیکنان
                    updatePlayers(deltaSeconds);

                    // بررسی اتصال‌های قطع شده
                    checkDisconnectedClients();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "خطا در به‌روزرسانی بازی", e);
                }
            } else {
                // خواب کوتاه برای کاهش مصرف CPU
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "وقفه در ترد به‌روزرسانی بازی", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * به‌روزرسانی تمام بازی‌های فعال
     */
    private void updateGames(float deltaTime) {
        for (GameLobby lobby : server.getGameLobbies().values()) {
            if (lobby.isGameActive()) {
                GameInstance game = server.getGameManager().getGameInstance(lobby.getId());
                if (game != null) {
                    game.update(deltaTime);

                    // هر یک ثانیه یک‌بار وضعیت بازی را به بازیکنان ارسال می‌کنیم
                    if (game.shouldSendUpdate()) {
                        lobby.broadcastMessage("GAME_STATE", game.getGameState().toString());
                    }
                }
            }
        }
    }

    /**
     * به‌روزرسانی وضعیت بازیکنان
     */
    private void updatePlayers(float deltaTime) {
        for (Map.Entry<String, ClientConnectionController> entry : server.getConnectedClients().entrySet()) {
            String username = entry.getKey();
            ClientConnectionController client = entry.getValue();

            // بازیابی انرژی بازیکنان در حالت استراحت
            GameLobby lobby = findPlayerLobby(username);
            if (lobby != null && lobby.isGameActive()) {
                GameInstance game = server.getGameManager().getGameInstance(lobby.getId());
                if (game != null) {
                    PlayerGameState playerState = game.getPlayerState(username);
                    if (playerState != null && !playerState.isMoving()) {
                        // بازیابی انرژی در حالت استراحت
                        playerState.restoreEnergy(deltaTime * 2);
                    }
                }
            }
        }
    }

    /**
     * بررسی اتصال‌های قطع شده
     */
    private void checkDisconnectedClients() {
        // پیاده‌سازی بررسی اتصال‌های قطع شده
    }

    /**
     * یافتن لابی بازیکن
     */
    private GameLobby findPlayerLobby(String username) {
        for (GameLobby lobby : server.getGameLobbies().values()) {
            if (lobby.hasPlayer(username)) {
                return lobby;
            }
        }
        return null;
    }

    /**
     * توقف ترد به‌روزرسانی
     */
    public void stop() {
        running = false;
    }
}
