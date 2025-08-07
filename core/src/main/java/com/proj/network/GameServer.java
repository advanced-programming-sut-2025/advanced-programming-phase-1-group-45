package com.proj.network;

import com.badlogic.gdx.Gdx;
import com.proj.Database.DatabaseHelper;
import com.proj.network.lobby.LobbyManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * سرور اصلی بازی
 */
public class GameServer {
    private static final int PORT = 8080;
    private static final int MAX_PLAYERS = 100;
    private static final int THREAD_POOL_SIZE = 10;
    private static final int MAINTENANCE_INTERVAL_SECONDS = 60;

    // نگهداری نشست‌های بازیکنان
    private final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();

    // نگهداری اطلاعات لابی‌های بازی
    private LobbyManager lobbyManager;

    // سرویس مدیریت ترد‌ها
    private final ExecutorService threadPool;
    private final ScheduledExecutorService maintenanceService;

    // سوکت سرور
    private ServerSocket serverSocket;

    // مدیریت کننده بازی
    private final GameManager gameManager;

    // مدیریت کننده پایگاه داده
    private final DatabaseHelper dbHelper;

    // وضعیت سرور
    private boolean running = false;

    public GameServer() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.maintenanceService = Executors.newScheduledThreadPool(1);
        this.gameManager = new GameManager(this);
        this.dbHelper = new DatabaseHelper();
        this.lobbyManager = new LobbyManager(this);
    }

    /**
     * شروع سرور بازی
     */
    public void start() {
        if (running) {
            Gdx.app.log("GameServer", "سرور قبلاً در حال اجراست");
            return;
        }

        try {
            // اتصال به پایگاه داده
            dbHelper.connect();

            // راه‌اندازی سوکت سرور
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT));

            running = true;
            Gdx.app.log("GameServer", "سرور بازی در پورت " + PORT + " شروع به کار کرد");

            // شروع ترد مدیریت بازی
            Thread gameUpdateThread = new Thread(new GameUpdateTask(this));
            gameUpdateThread.setDaemon(true);
            gameUpdateThread.start();

            // زمانبندی وظایف نگهداری سرور
            scheduleMaintenanceTasks();

            // پذیرش اتصالات جدید
            acceptConnections();

        } catch (IOException e) {
            Gdx.app.error("GameServer", "خطا در راه‌اندازی سرور", e);
        } finally {
            shutdown();
        }
    }

    /**
     * زمانبندی وظایف نگهداری سرور
     */
    private void scheduleMaintenanceTasks() {
        maintenanceService.scheduleAtFixedRate(() -> {
            try {
                // بررسی اتصال‌های قطع شده
                checkDisconnectedClients();

                // بررسی لابی‌های غیرفعال
                checkInactiveLobbies();

                // بررسی بازی‌های غیرفعال
                gameManager.checkInactiveGames();

                // ارسال آمار سرور
                logServerStats();
            } catch (Exception e) {
                Gdx.app.error("GameServer", "خطا در اجرای وظایف نگهداری", e);
            }
        }, MAINTENANCE_INTERVAL_SECONDS, MAINTENANCE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * پذیرش اتصالات جدید
     */
    private void acceptConnections() {
        try {
            while (running && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                Gdx.app.log("GameServer", "اتصال جدید از: " + clientSocket.getInetAddress());

                if (connectedClients.size() >= MAX_PLAYERS) {
                    Gdx.app.log("GameServer", "سرور پر است. اتصال جدید رد شد.");
                    sendServerFullMessage(clientSocket);
                    clientSocket.close();
                    continue;
                }

                // ایجاد هندلر جدید برای کلاینت
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            if (running && !serverSocket.isClosed()) {
                Gdx.app.error("GameServer", "خطا در پذیرش اتصالات", e);
            }
        }
    }

    /**
     * ارسال پیام پر بودن سرور
     */
    private void sendServerFullMessage(Socket socket) {
        try {
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            out.println("{\"type\":\"ERROR\",\"data\":\"سرور پر است. لطفاً بعداً تلاش کنید.\"}");
        } catch (IOException e) {
            Gdx.app.error("GameServer", "خطا در ارسال پیام پر بودن سرور", e);
        }
    }

    /**
     * ثبت کلاینت جدید
     */
    public void registerClient(String username, ClientHandler handler) {
        connectedClients.put(username, handler);
        broadcastSystemMessage(username + " به بازی وارد شد");
        Gdx.app.log("GameServer", "کاربر ثبت شد: " + username);
    }

    /**
     * حذف کلاینت
     */
    public void removeClient(String username) {
        connectedClients.remove(username);

        // خروج بازیکن از لابی‌ها
        lobbyManager.removePlayer(username);

        broadcastSystemMessage(username + " از بازی خارج شد");
        Gdx.app.log("GameServer", "کاربر حذف شد: " + username);
    }

    /**
     * ارسال پیام سیستمی به همه کاربران
     */
    public void broadcastSystemMessage(String message) {
        for (ClientHandler handler : connectedClients.values()) {
            handler.sendMessage("SYSTEM", message);
        }
    }

    /**
     * ایجاد لابی جدید
     */
    public void createLobby(String lobbyName, String owner, String password, int maxPlayers, boolean isPrivate, boolean isVisible) {
        GameLobby lobby = lobbyManager.createAndGetLobby(lobbyName, owner, password, maxPlayers, isPrivate, isVisible);
        ClientHandler ownerHandler = connectedClients.get(owner);
        if (ownerHandler != null) {
            lobby.addPlayer(owner, ownerHandler);
            ownerHandler.sendMessage("LOBBY_CREATED", lobby.getLobbyInfo().toString());
        }

        Gdx.app.log("GameServer", "lobby created:  " + lobbyName + " با ID: " + lobby.getId());
    }

    /**
     * پیوستن به لابی
     */
    public void joinLobby(String lobbyId, String username, String password) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientHandler client = connectedClients.get(username);

        if (client == null) {
            Gdx.app.error("GameServer", "Username not found: " + username);
            return;
        }

        if (lobby == null) {
            client.sendMessage("ERROR", "Lobby not found");
            return;
        }

        if (lobby.isFull()) {
            client.sendMessage("ERROR", "Lobby is full: " + lobby.getId());
            return;
        }

        if (lobby.isGameActive() && !lobby.hasPlayer(username)) {
            client.sendMessage("ERROR", "The game in this lobby is already active");
            return;
        }

        if (lobby.isPrivate() && !lobby.checkPassword(password)) {
            client.sendMessage("ERROR", "Incorrect password");
            return;
        }

        GameLobby currentLobby = findPlayerLobby(username);
        if (currentLobby != null && !currentLobby.getId().equals(lobbyId)) {
            currentLobby.removePlayer(username);
        }

        lobby.addPlayer(username, client);
        client.sendMessage("JOIN_SUCCESS", lobby.getId());

        // اطلاع به سایر بازیکنان لابی
        lobby.broadcastMessage("SYSTEM", username + "Joined lobby");
    }

    /**
     * شروع بازی
     */
    public void startGame(String lobbyId, String username) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientHandler client = connectedClients.get(username);

        if (client == null) {
            Gdx.app.error("GameServer", "کاربر یافت نشد: " + username);
            return;
        }

        if (lobby == null) {
            client.sendMessage("ERROR", "لابی مورد نظر یافت نشد");
            return;
        }

        if (!lobby.getOwner().equals(username)) {
            client.sendMessage("ERROR", "فقط صاحب لابی می‌تواند بازی را شروع کند");
            return;
        }

        if (lobby.getPlayerCount() < 2) {
            client.sendMessage("ERROR", "حداقل 2 بازیکن برای شروع بازی لازم است");
            return;
        }

        if (lobby.isGameActive()) {
            client.sendMessage("ERROR", "بازی قبلاً شروع شده است");
            return;
        }

        // شروع بازی
        boolean success = gameManager.startGame(lobby);

        if (success) {
            // اطلاع به همه بازیکنان
            lobby.broadcastMessage("GAME_STARTED", "بازی شروع شد");
        } else {
            client.sendMessage("ERROR", "خطا در شروع بازی");
        }
    }

    /**
     * پردازش اکشن بازی
     */
    public void processGameAction(String username, String action, String actionData) {
        // یافتن لابی بازیکن
        GameLobby playerLobby = findPlayerLobby(username);

        if (playerLobby == null || !playerLobby.isGameActive()) {
            ClientHandler client = connectedClients.get(username);
            if (client != null) {
                client.sendMessage("ERROR", "شما در هیچ بازی فعالی نیستید");
            }
            return;
        }

        // پردازش اکشن بازی
        gameManager.processGameAction(playerLobby, username, action, actionData);
    }

    /**
     * یافتن لابی بازیکن
     */
    public GameLobby findPlayerLobby(String username) {
        for (GameLobby lobby : lobbyManager.getGameLobbies()) {
            if (lobby.hasPlayer(username)) {
                return lobby;
            }
        }
        return null;
    }

    /**
     * تولید شناسه منحصر به فرد برای لابی
     */
    private String generateLobbyId() {
        return "lobby_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * بررسی اتصال‌های قطع شده
     */
    private void checkDisconnectedClients() {
        for (Map.Entry<String, ClientHandler> entry : new ConcurrentHashMap<>(connectedClients).entrySet()) {
            ClientHandler handler = entry.getValue();
            if (handler.isTimedOut()) {
                Gdx.app.log("GameServer", "کاربر به دلیل عدم فعالیت حذف شد: " + entry.getKey());
                handler.shutdown();
                removeClient(entry.getKey());
            }
        }
    }

    /**
     * بررسی لابی‌های غیرفعال
     */
    private void checkInactiveLobbies() {
        long inactivityTimeout = 30 * 60 * 1000; // 30 دقیقه

        for (Map.Entry<String, GameLobby> entry : new ConcurrentHashMap<>(lobbyManager.getGameLobbiesMap()).entrySet()) {
            GameLobby lobby = entry.getValue();

            // اگر لابی خالی است یا مدت زیادی غیرفعال بوده
            if (lobby.isEmpty() || (lobby.isInactive(inactivityTimeout) && !lobby.isGameActive())) {
                lobbyManager.getGameLobbiesMap().remove(entry.getKey());
                Gdx.app.log("GameServer", "لابی غیرفعال حذف شد: " + entry.getKey());
            }
        }
    }

    /**
     * ثبت آمار سرور
     */
    private void logServerStats() {
        Gdx.app.log("GameServer", String.format(
            "آمار سرور: %d کاربر آنلاین, %d لابی فعال, %d بازی در حال اجرا",
            connectedClients.size(),
            lobbyManager.getGameLobbiesMap().size(),
            gameManager.getActiveGamesCount()
        ));
    }

    /**
     * بستن سرور
     */
    public void shutdown() {
        if (!running) {
            return;
        }

        running = false;

        try {
            // اطلاع به همه کاربران
            broadcastSystemMessage("سرور در حال بسته شدن است");

            // بستن همه اتصالات
            for (ClientHandler handler : connectedClients.values()) {
                handler.shutdown();
            }
            connectedClients.clear();

            // بستن سوکت سرور
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // بستن سرویس‌های ترد
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }

            maintenanceService.shutdownNow();

            // بستن اتصال پایگاه داده
            dbHelper.disconnect();

            Gdx.app.log("GameServer", "سرور با موفقیت بسته شد");
        } catch (IOException e) {
            Gdx.app.error("GameServer", "خطا در بستن سرور", e);
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }

    public Map<String, ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public Map<String, GameLobby> getGameLobbies() {
        return lobbyManager.getGameLobbiesMap();
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public boolean isRunning() {
        return running;
    }
}
