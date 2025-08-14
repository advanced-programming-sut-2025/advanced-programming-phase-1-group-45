package com.proj.network;

import com.proj.Database.DatabaseHelper;
import com.proj.network.lobby.GameLobby;
import com.proj.network.lobby.LobbyManager;
import com.proj.network.message.JsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class GameServer {
    private static final int port = 8080;
    private static final int MAX_PLAYERS = 100;
    private static final int threadPoolNum = 10;

    private final Map<String, ClientConnectionController> connectedClients = new ConcurrentHashMap<>();
    private final LobbyManager lobbyManager;
    private final ExecutorService threadPool;
    private final ScheduledExecutorService maintenanceService;
    private final GameManager gameManager;
    private final DatabaseHelper dbHelper;

    private ServerSocket serverSocket;
    private boolean running = false;

    public GameServer() {
        this.threadPool = Executors.newFixedThreadPool(threadPoolNum);
        this.maintenanceService = Executors.newScheduledThreadPool(1);
        this.gameManager = new GameManager(this);
        this.dbHelper = new DatabaseHelper();
        this.lobbyManager = new LobbyManager(this);
    }

    public void start() {
        if (running) {
            System.out.println("GameServer " +  "server is already running");
            return;
        }
        try {
            dbHelper.connect();
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            running = true;
            System.out.println("Game server started on port " + port);
            Thread gameUpdateThread = new Thread(new GameUpdateTask(this));
            gameUpdateThread.setDaemon(true);
            gameUpdateThread.start();
            listener();
        } catch (IOException e) {
            System.out.println("error starting server" +  e);
        } finally {
            shutdown();
        }
    }

    private void listener() {
        try {
            while (running && !serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                if (connectedClients.size() >= MAX_PLAYERS) {
                    System.out.println("Server is full. New connection rejected.");
                    clientSocket.close();
                    continue;
                }

                ClientConnectionController clientConnectionController = new ClientConnectionController(clientSocket, this);
                threadPool.execute(clientConnectionController);
            }
        } catch (IOException e) {
            if (running && !serverSocket.isClosed()) {
                System.out.println("Server can not accept connections" + e);
            }
        }
    }


    public void loginClient(String username, ClientConnectionController clientController) {
        if (connectedClients.containsKey(username)) {
            ClientConnectionController oldHandler = connectedClients.get(username);
            oldHandler.sendMessage("DISCONNECT", JsonBuilder.create()
                .put("message", "New login detected")
                .build());
            oldHandler.shutdown();
        }
        connectedClients.put(username, clientController);
        System.out.println("User accepted: " + username);
        notifyPlayerStatusUpdate();
    }

    public void removeClient(String username) {
        connectedClients.remove(username);
        lobbyManager.removePlayer(username);
        broadcast(username + " left the game");
        System.out.println("User removed: " + username);
        notifyPlayerStatusUpdate();
    }

    public void broadcast(String message) {
        JSONObject messageObj = new JSONObject();
        messageObj.put("type", "SYSTEM");

        JSONObject data = new JSONObject();
        data.put("message", message);
        messageObj.put("data", data);

        String jsonMessage = messageObj.toString();

        for (Map.Entry<String, ClientConnectionController> entry : new ConcurrentHashMap<>(connectedClients).entrySet()) {
            ClientConnectionController clientController = entry.getValue();
            if (clientController != null && clientController.isRunning()) {
                clientController.sendRaw(jsonMessage);
            }
        }
    }

    public void createLobby(String lobbyName, String admin, String password, int maxPlayers, boolean isPrivate, boolean isVisible) {
        System.out.println("GameServer " +  "Creating a new lobby: " + lobbyName);

        GameLobby lobby = lobbyManager.createAndGetLobby(lobbyName, admin, password, maxPlayers, isPrivate, isVisible);
        ClientConnectionController adminThread = connectedClients.get(admin);
        if (adminThread != null) {
            lobby.addPlayer(admin, adminThread);
            JSONObject response = new JSONObject();
            response.put("type", "LOBBY_CREATED");
            response.put("data", lobby.getLobbyInfo());
            adminThread.sendRaw(response.toString());
            broadcastLobbiesList();
        }
        System.out.println("GameServer " +  "Lobby created: " + lobbyName + " with ID: " + lobby.getId());
    }
    public void broadcastLobbiesList() {
        for (ClientConnectionController clientController : connectedClients.values()) {
            clientController.sendLobbiesList();
        }
    }

    public void joinLobby(String lobbyId, String username, String password) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientConnectionController client = connectedClients.get(username);

        if (client == null) {
            System.out.println("GameServer " +  "Username not found: " + username);
            return;
        }

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("type", "ERROR");
        JSONObject errorData = new JSONObject();

        if (lobby.isFull()) {
            errorData.put("code", "LOBBY_FULL");
            errorData.put("message", "Lobby is full: " + lobby.getId());
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
            return;
        }

        if (lobby.isGameActive()) {
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
    }

    public void startGame(String lobbyId, String username) {
        GameLobby lobby = lobbyManager.getGameLobby(lobbyId);
        ClientConnectionController client = connectedClients.get(username);

        if (client == null) {
            System.out.println("username not found: " + username);
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

        if (!lobby.getAdmin().equals(username)) {
            errorData.put("code", "NOT_ADMIN");
            errorData.put("message", "Only lobby admin can start the game");
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

            JSONObject broadCast = JsonBuilder.create().put("message", "Game Started").put("data", JsonBuilder.empty()).build();
            broadCast.put("type", "GAME_STARTED");

            lobby.broadcastRaw(broadCast.toString());
        } else {
            errorData.put("code", "START_FAILED");
            errorData.put("message", "Error starting game");
            errorResponse.put("data", errorData);
            client.sendRaw(errorResponse.toString());
        }
    }

    public GameLobby findPlayerLobby(String username) {
        for (GameLobby lobby : lobbyManager.getGameLobbies()) {
            if (lobby.hasPlayer(username)) {
                return lobby;
            }
        }
        return null;
    }

    private void checkDisconnectedClients() {
        for (Map.Entry<String, ClientConnectionController> entry : new ConcurrentHashMap<>(connectedClients).entrySet()) {
            ClientConnectionController clientController = entry.getValue();
            if (clientController.isTimedOut()) {
                System.out.println("GameServer " +  "User removed due to inactivity: " + entry.getKey());
                clientController.shutdown();
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
                System.out.println("GameServer " +  "Inactive lobby removed: " + entry.getKey());
            }
        }
    }

    private void logServerStats() {
        System.out.println("GameServer " +  String.format(
            "Server stats: %d online users, %d active lobbies, %d running games",
            connectedClients.size(),
            lobbyManager.getGameLobbiesMap().size(),
            gameManager.getActiveGamesCount()
        ));
    }

    public JSONObject sendOnlinePlayersList() {
        JSONArray playersArray = new JSONArray();

        for (ClientConnectionController clientController : connectedClients.values()) {
            JSONObject playerInfo = new JSONObject();
            playerInfo.put("username", clientController.getUsername());
            playerInfo.put("status", "Online");

            if (findPlayerLobby(clientController.getUsername()) != null) {
                GameLobby lobby = findPlayerLobby(clientController.getUsername());
                playerInfo.put("lobbyId", lobby.getId());
                playerInfo.put("lobbyName", lobby.getName());
                playerInfo.put("inLobby", true);
                playerInfo.put("isAdmin", lobby.getAdmin().equals(clientController.getUsername()));
            } else {
                playerInfo.put("inLobby", false);
            }

            playersArray.put(playerInfo);
        }

        JSONObject result = JsonBuilder.create().put("onlinePlayers", playersArray).build();
        return result;
    }

    public void notifyPlayerStatusUpdate() {
        for (ClientConnectionController clientController : connectedClients.values()) {
            clientController.sendPlayerList();
        }
    }

    public void shutdown() {
        if (!running) {
            return;
        }

        running = false;

        try {
            broadcast("Server is shutting down");

            for (ClientConnectionController clientController : connectedClients.values()) {
                clientController.shutdown();
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

            System.out.println("GameServer" +  "Server closed successfully");
        } catch (IOException e) {
            System.out.println("GameServer" +  "Error closing server" + e);
        }
    }

    public DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }

    public Map<String, ClientConnectionController> getConnectedClients() {
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
