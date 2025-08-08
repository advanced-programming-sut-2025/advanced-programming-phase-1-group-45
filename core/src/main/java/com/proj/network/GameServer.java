package com.proj.network;

import com.proj.Database.DatabaseHelper;
import com.proj.network.lobby.GameLobby;
import com.proj.network.lobby.LobbyManager;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT = 8080;
    private static final int MAX_PLAYERS = 100;
    private static final int THREAD_POOL_SIZE = 10;
    private static final int MAINTENANCE_INTERVAL_SECONDS = 60;

    private final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    private final LobbyManager lobbyManager;
    private final ExecutorService threadPool;
    private final ScheduledExecutorService maintenanceService;
    private final GameManager gameManager;
    private final DatabaseHelper dbHelper;

    private ServerSocket serverSocket;
    private boolean running = false;

    public GameServer() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.maintenanceService = Executors.newScheduledThreadPool(1);
        this.gameManager = new GameManager(this);
        this.dbHelper = new DatabaseHelper();
        this.lobbyManager = new LobbyManager(this);
    }

    public void start() {
        if (running) {
            System.err.println("GameServer " +  "server is already running");
            return;
        }

        try {
            dbHelper.connect();
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT));

            running = true;
            System.err.println("GameServer " +  "Game server started on port " + PORT);

            Thread gameUpdateThread = new Thread(new GameUpdateTask(this));
            gameUpdateThread.setDaemon(true);
            gameUpdateThread.start();

            scheduleMaintenanceTasks();
            acceptConnections();

        } catch (IOException e) {
            System.err.println("GameServer " +  "Error starting server" +  e);
        } finally {
            shutdown();
        }
    }

    private void scheduleMaintenanceTasks() {
        maintenanceService.scheduleAtFixedRate(() -> {
            try {
                checkDisconnectedClients();
                checkInactiveLobbies();
                gameManager.checkInactiveGames();
                logServerStats();
            } catch (Exception e) {
                System.err.println("GameServer " +  "Error in maintenance tasks" +  e);
            }
        }, MAINTENANCE_INTERVAL_SECONDS, MAINTENANCE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

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

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            if (running && !serverSocket.isClosed()) {
                System.err.println("GameServer " +  "Error accepting connections" + e);
            }
        }
    }

    private void sendServerFullMessage(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            JSONObject response = new JSONObject();
            response.put("type", "ERROR");

            JSONObject data = new JSONObject();
            data.put("code", "SERVER_FULL");
            data.put("message", "Server is full. Please try again later.");

            response.put("data", data);
            out.println(response.toString());
        } catch (IOException e) {
            System.err.println("GameServer " +  "Error sending server full message" + e);
        }
    }

    public void registerClient(String username, ClientHandler handler) {
        connectedClients.put(username, handler);
        broadcastSystemMessage(username + " joined the game");
        System.err.println("GameServer " +  "User registered: " + username);
    }

    public void removeClient(String username) {
        connectedClients.remove(username);
        lobbyManager.removePlayer(username);
        broadcastSystemMessage(username + " left the game");
        System.err.println("GameServer " +  "User removed: " + username);
    }

    public void broadcastSystemMessage(String message) {
        JSONObject messageObj = new JSONObject();
        messageObj.put("type", "SYSTEM");

        JSONObject data = new JSONObject();
        data.put("message", message);
        messageObj.put("data", data);

        String jsonMessage = messageObj.toString();

        for (ClientHandler handler : connectedClients.values()) {
            handler.sendRaw(jsonMessage);
        }
    }

    public void createLobby(String lobbyName, String owner, String password, int maxPlayers, boolean isPrivate, boolean isVisible) {
        System.err.println("GameServer " +  "Creating a new lobby: " + lobbyName);

        GameLobby lobby = lobbyManager.createAndGetLobby(lobbyName, owner, password, maxPlayers, isPrivate, isVisible);
        ClientHandler ownerHandler = connectedClients.get(owner);
        if (ownerHandler != null) {
            lobby.addPlayer(owner, ownerHandler);

            JSONObject response = new JSONObject();
            response.put("type", "LOBBY_CREATED");
            response.put("data", lobby.getLobbyInfo());

            ownerHandler.sendRaw(response.toString());
        }

        System.err.println("GameServer " +  "Lobby created: " + lobbyName + " with ID: " + lobby.getId());
    }

    public void joinLobby(String lobbyId, String username, String password) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientHandler client = connectedClients.get(username);

        if (client == null) {
            System.err.println("GameServer " +  "Username not found: " + username);
            return;
        }

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("type", "ERROR");
        JSONObject errorData = new JSONObject();

        if (lobby == null) {
            errorData.put("code", "LOBBY_NOT_FOUND");
            errorData.put("message", "Lobby not found");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.isFull()) {
            errorData.put("code", "LOBBY_FULL");
            errorData.put("message", "Lobby is full: " + lobby.getId());
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.isGameActive() && !lobby.hasPlayer(username)) {
            errorData.put("code", "GAME_ACTIVE");
            errorData.put("message", "The game in this lobby is already active");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.isPrivate() && !lobby.checkPassword(password)) {
            errorData.put("code", "INVALID_PASSWORD");
            errorData.put("message", "Incorrect password");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        GameLobby currentLobby = findPlayerLobby(username);
        if (currentLobby != null && !currentLobby.getId().equals(lobbyId)) {
            currentLobby.removePlayer(username);
        }

        lobby.addPlayer(username, client);

        JSONObject successResponse = new JSONObject();
        successResponse.put("type", "JOIN_SUCCESS");
        successResponse.put("data", lobby.getLobbyInfo());
        client.sendRaw(successResponse.toString());

        // Broadcast to lobby
        JSONObject broadcast = new JSONObject();
        broadcast.put("type", "PLAYER_JOINED");

        JSONObject broadcastData = new JSONObject();
        broadcastData.put("username", username);
        broadcast.put("data", broadcastData);

        lobby.broadcastRaw(broadcast.toString());
    }

    public void startGame(String lobbyId, String username) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientHandler client = connectedClients.get(username);

        if (client == null) {
            System.err.println("GameServer " +  "Username not found: " + username);
            return;
        }

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("type", "ERROR");
        JSONObject errorData = new JSONObject();

        if (lobby == null) {
            errorData.put("code", "LOBBY_NOT_FOUND");
            errorData.put("message", "Lobby not found");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (!lobby.getOwner().equals(username)) {
            errorData.put("code", "NOT_OWNER");
            errorData.put("message", "Only lobby owner can start the game");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.getPlayerCount() < 2) {
            errorData.put("code", "MIN_PLAYERS");
            errorData.put("message", "Minimum 2 players required to start");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.isGameActive()) {
            errorData.put("code", "ALREADY_STARTED");
            errorData.put("message", "Game already started");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        boolean success = gameManager.startGame(lobby);

        if (success) {
            JSONObject broadcast = new JSONObject();
            broadcast.put("type", "GAME_STARTED");

            JSONObject data = new JSONObject();
            data.put("message", "Game started");
            broadcast.put("data", data);

            lobby.broadcastRaw(broadcast.toString());
        } else {
            errorData.put("code", "START_FAILED");
            errorData.put("message", "Error starting game");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
        }
    }

    public void processGameAction(String username, String action, String actionData) {
        GameLobby playerLobby = findPlayerLobby(username);

        if (playerLobby == null || !playerLobby.isGameActive()) {
            ClientHandler client = connectedClients.get(username);
            if (client != null) {
                JSONObject error = new JSONObject();
                error.put("type", "ERROR");

                JSONObject errorData = new JSONObject();
                errorData.put("code", "NOT_IN_GAME");
                errorData.put("message", "You are not in any active game");
                error.put("data", errorData);

                client.sendRaw(error.toString());
            }
            return;
        }

        gameManager.processGameAction(playerLobby, username, action, actionData);
    }

    public GameLobby findPlayerLobby(String username) {
        for (GameLobby lobby : lobbyManager.getGameLobbies()) {
            if (lobby.hasPlayer(username)) {
                return lobby;
            }
        }
        return null;
    }

    private String generateLobbyId() {
        return "lobby_" + UUID.randomUUID().toString().substring(0, 8);
    }

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

    private void checkInactiveLobbies() {
        long inactivityTimeout = 30 * 60 * 1000;

        for (Map.Entry<String, GameLobby> entry : new ConcurrentHashMap<>(lobbyManager.getGameLobbiesMap()).entrySet()) {
            GameLobby lobby = entry.getValue();
            if (lobby.isEmpty() || (lobby.isInactive(inactivityTimeout) && !lobby.isGameActive())) {
                lobbyManager.getGameLobbiesMap().remove(entry.getKey());
                System.err.println("GameServer " +  "Inactive lobby removed: " + entry.getKey());
            }
        }
    }

    private void logServerStats() {
        System.err.println("GameServer " +  String.format(
            "Server stats: %d online users, %d active lobbies, %d running games",
            connectedClients.size(),
            lobbyManager.getGameLobbiesMap().size(),
            gameManager.getActiveGamesCount()
        ));
    }

    public void shutdown() {
        if (!running) {
            return;
        }

        running = false;

        try {
            broadcastSystemMessage("Server is shutting down");

            for (ClientHandler handler : connectedClients.values()) {
                handler.shutdown();
            }
            connectedClients.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

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
