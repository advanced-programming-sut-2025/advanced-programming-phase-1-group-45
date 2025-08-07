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
            System.err.println("GameServer " +  "server is already running");
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
            System.err.println("GameServer " +  "Game server started on port " + PORT);
            // شروع ترد مدیریت بازی
            Thread gameUpdateThread = new Thread(new GameUpdateTask(this));
            gameUpdateThread.setDaemon(true);
            gameUpdateThread.start();

            // زمانبندی وظایف نگهداری سرور
            scheduleMaintenanceTasks();

            // پذیرش اتصالات جدید
            acceptConnections();

        } catch (IOException e) {
            System.err.println("GameServer " +  "Error starting server" +  e);
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
                System.err.println("GameServer " +  "Error in maintenance tasks" +  e);            }
        }, MAINTENANCE_INTERVAL_SECONDS, MAINTENANCE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * پذیرش اتصالات جدید
     */
    private void acceptConnections() {
        try {
            while (running && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("GameServer " +  "New connection from: " + clientSocket.getInetAddress());

                if (connectedClients.size() >= MAX_PLAYERS) {
                    System.err.println("GameServer " +  "Server is full. New connection rejected.");
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
                System.err.println("GameServer " +  "Error accepting connections" + e);
            }
        }
    }

    /**
     * ارسال پیام پر بودن سرور
     */
    private void sendServerFullMessage(Socket socket) {
        try {
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            out.println("{\"type\":\"ERROR\",\"data\":\"Server is full. Please try again later.\"}");
        } catch (IOException e) {
            System.err.println("GameServer " +  "Error sending server full message" + e);
        }
    }

    /**
     * ثبت کلاینت جدید
     */
    public void registerClient(String username, ClientHandler handler) {
        connectedClients.put(username, handler);
        broadcastSystemMessage(username + " joined the game");
        System.err.println("GameServer " +  "User registered: " + username);
    }

    /**
     * حذف کلاینت
     */
    public void removeClient(String username) {
        connectedClients.remove(username);

        // خروج بازیکن از لابی‌ها
        lobbyManager.removePlayer(username);

        broadcastSystemMessage(username + " left the game");
        System.err.println("GameServer " +  "User removed: " + username);
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
        System.err.println("GameServer " +  "Creating a new lobby: " + lobbyName);

        GameLobby lobby = lobbyManager.createAndGetLobby(lobbyName, owner, password, maxPlayers, isPrivate, isVisible);
        ClientHandler ownerHandler = connectedClients.get(owner);
        if (ownerHandler != null) {
            lobby.addPlayer(owner, ownerHandler);
            ownerHandler.sendMessage("LOBBY_CREATED", lobby.getLobbyInfo().toString());
        }

        System.err.println("GameServer " +  "Lobby created: " + lobbyName + " with ID: " + lobby.getId());
    }

    /**
     * پیوستن به لابی
     */
    public void joinLobby(String lobbyId, String username, String password) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientHandler client = connectedClients.get(username);

        if (client == null) {
            System.err.println("GameServer " +  "Username not found: " + username);
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
            System.err.println("GameServer " +  "Username not found: " + username);
            return;
        }

        if (lobby == null) {
            client.sendMessage("ERROR", "Lobby not found");
            return;
        }

        if (!lobby.getOwner().equals(username)) {
            client.sendMessage("ERROR", "Only lobby owner can start the game");
            return;
        }

        if (lobby.getPlayerCount() < 2) {
            client.sendMessage("ERROR", "Minimum 2 players required to start");
            return;
        }

        if (lobby.isGameActive()) {
            client.sendMessage("ERROR", "Game already started");
            return;
        }

        // شروع بازی
        boolean success = gameManager.startGame(lobby);

        if (success) {
            lobby.broadcastMessage("GAME_STARTED", "Game started");
        } else {
            client.sendMessage("ERROR", "Error starting game");
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
                client.sendMessage("ERROR", "You are not in any active game");
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
                System.err.println("GameServer " +  "User removed due to inactivity: " + entry.getKey());
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
                System.err.println("GameServer " +  "Inactive lobby removed: " + entry.getKey());
            }
        }
    }

    /**
     * ثبت آمار سرور
     */
    private void logServerStats() {
        System.err.println("GameServer " +  String.format(
            "Server stats: %d online users, %d active lobbies, %d running games",
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
            broadcastSystemMessage("Server is shutting down");

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


            System.err.println("GameServer" +  "Server closed successfully");
        } catch (IOException e) {
            System.err.println("GameServer" +  "Error closing server" + e);
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
