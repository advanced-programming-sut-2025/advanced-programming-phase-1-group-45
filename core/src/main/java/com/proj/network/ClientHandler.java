package com.proj.network;

import com.badlogic.gdx.Gdx;
import com.proj.network.lobby.GameLobby;
import com.proj.network.message.JsonBuilder;
import com.proj.network.message.JsonParser;
import com.proj.network.message.NetworkMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final GameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username = null;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private long lastActivityTime;
    private static final long TIMEOUT_MS = 60000; // 1 minute timeout
    private GameLobby currentLobby;

    public ClientHandler(Socket socket, GameServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.lastActivityTime = System.currentTimeMillis();
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("ClientHandler initialization error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if (!login()) {
                return;
            }

            String inputLine;
            while (running.get() && (inputLine = in.readLine()) != null) {
                updateLastActivity();
                try {
                    NetworkMessage message = NetworkMessage.parse(inputLine);
                    processMessage(message);
                } catch (IOException e) {
                    sendError("INVALID_FORMAT", "Invalid message format");
                } catch (Exception e) {
                    System.err.println("Message processing error: " + e.getMessage());
                    sendError("PROCESSING_ERROR", "Error processing request");
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            if (username != null) {
                server.removeClient(username);
            }
            shutdown();
        }
    }


    private boolean loggedIn = false;
    public boolean getLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    private boolean login() throws IOException {
        // Send structured auth request
        JSONObject requestData = JsonBuilder.create()
            .put("message", "Please enter your credentials")
            .build();
        sendMessage("AUTH_REQUEST", requestData);

        String response = in.readLine();
        if (response == null) {
            return false;
        }

        try {
            NetworkMessage authMessage = NetworkMessage.parse(response);
            if (!"AUTH".equals(authMessage.getType())) {
                sendError("AUTH_FAILED", "Invalid request type");
                return false;
            }

            JSONObject credentials = authMessage.getData();
            String username = JsonParser.getString(credentials, "username", "");
            String password = JsonParser.getString(credentials, "password", "");

            if (username.isEmpty() || password.isEmpty()) {
                sendError("AUTH_FAILED", "Missing username or password");
                return false;
            }

//            if (loggedIn) {
                // Handle existing connections
                if (server.getConnectedClients().containsKey(username)) {
                    ClientHandler existingClient = server.getConnectedClients().get(username);
                    existingClient.sendMessage("DISCONNECT", JsonBuilder.create()
                        .put("message", "You've been disconnected because you logged in from another device")
                        .build());
                    existingClient.shutdown();
                }
                this.username = username;
                server.registerClient(username, this);

                // Send structured auth success
                sendMessage("AUTH_SUCCESS", JsonBuilder.create()
                    .put("message", "Welcome " + username)
                    .put("username", username)
                    .build());
                return true;
//            } else {
//                sendError("AUTH_FAILED", "Registration failed");
//                return false;
//            }
        } catch (Exception e) {
            sendError("AUTH_FAILED", "Invalid authentication format");
            return false;
        }
    }

    private void processMessage(NetworkMessage message) {
        String type = message.getType();
        JSONObject data = message.getData();

        switch (type) {
            case "CHAT":
                processChatMessage(data);
                break;

            case "CREATE_LOBBY":
                createLobby(data);
                break;

            case "JOIN_LOBBY":
                joinLobby(data);
                break;

            case "START_GAME":
                startGame(data);
                break;

            case "GAME_ACTION":
                processGameAction(data);
                break;

            case "GET_ONLINE_PLAYERS" :
                sendPlayerList();
                break;

            case "GET_LOBBIES":
                sendLobbiesList();
                break;

            case "LEAVE_LOBBY":
                leaveLobby(data);
                break;

            case "PING":
                sendMessage("PONG", JsonBuilder.empty());
                break;

            default:
                Gdx.app.log("ClientHandler", "Unknown message type: " + type);
                sendError("UNKNOWN_TYPE", "Invalid message type");
                break;
        }
    }

    private void processChatMessage(JSONObject data) {
        String messageText = JsonParser.getString(data, "message", "");
        if (messageText.trim().isEmpty()) {
            return;
        }

        // Truncate long messages
        if (messageText.length() > 200) {
            messageText = messageText.substring(0, 200) + "...";
        }

        String recipient = JsonParser.getString(data, "recipient", null);
        if (recipient != null) {
            // Private message
            ClientHandler recipientHandler = server.getConnectedClients().get(recipient);
            if (recipientHandler != null) {
                JSONObject privateMsg = JsonBuilder.create()
                    .put("sender", username)
                    .put("message", messageText)
                    .put("isPrivate", true)
                    .build();

                recipientHandler.sendMessage("PRIVATE_CHAT", privateMsg);
                // Also send a copy to the sender
                sendMessage("PRIVATE_CHAT", privateMsg);
            } else {
                sendError("USER_OFFLINE", "User " + recipient + " is not online");
            }
        } else {
            // Broadcast public message
            JSONObject publicMsg = JsonBuilder.create()
                .put("sender", username)
                .put("message", messageText)
                .put("isPrivate", false)
                .build();
            server.broadcastSystemMessage("CHAT" + messageText);
        }
    }

    public void sendRaw(String message) {
        if (!running.get() || out == null) return;
        out.println(message);
        out.flush();

        if (out.checkError()) {
            shutdown();
        }
    }

    private void createLobby(JSONObject data) {
        String name = JsonParser.getString(data, "name", "");
        int maxPlayers = JsonParser.getInt(data, "maxPlayers", 4);
        boolean isPrivate = JsonParser.getBoolean(data, "isPrivate", false);
        boolean isVisible = JsonParser.getBoolean(data, "isVisible", true);
        String password = JsonParser.getString(data, "password", "");

        if (name.trim().isEmpty()) {
            sendError("LOBBY_CREATION_FAILED", "Lobby name must not be empty");
            return;
        }

        if (maxPlayers < 2 || maxPlayers > 8) {
            sendError("LOBBY_CREATION_FAILED", "Players number should be between 2 and 8");
            return;
        }

        if (isPrivate && password.isEmpty()) {
            sendError("LOBBY_CREATION_FAILED", "Private Lobby should have a password");
            return;
        }

        try {
            server.createLobby(name, username, password, maxPlayers, isPrivate, isVisible);
        } catch (Exception e) {
            sendError("LOBBY_CREATION_FAILED", "Error creating lobby: " + e.getMessage());
        }
    }

    private void joinLobby(JSONObject data) {
        String lobbyId = JsonParser.getString(data, "lobbyId", "");
        String password = JsonParser.getString(data, "password", "");

        if (lobbyId.isEmpty()) {
            sendError("JOIN_FAILED", "Lobby ID is required");
            return;
        }

        try {
            server.joinLobby(lobbyId, username, password);
        } catch (Exception e) {
            sendError("JOIN_FAILED", "Error joining lobby: " + e.getMessage());
        }
    }

    private void leaveLobby(JSONObject data) {
        GameLobby lobby = server.findPlayerLobby(username);
        if (lobby != null) {
            lobby.removePlayer(username);
            setCurrentLobby(null);
            sendMessage("LEAVE_SUCCESS", JsonBuilder.create()
                .put("message", "You left the lobby")
                .build());

            if (lobby.isEmpty()) {
                server.getGameLobbies().remove(lobby.getId());
                server.broadcastSystemMessage("Lobby: " + lobby.getName() + " was removed");
            }
        } else {
            sendError("NOT_IN_LOBBY", "You are not in any lobby");
        }
    }


    private void startGame(JSONObject data) {
        String lobbyId = JsonParser.getString(data, "lobbyId", "");
        if (lobbyId.isEmpty()) {
            sendError("GAME_START_FAILED", "Lobby ID is required");
            return;
        }

        try {
            server.startGame(lobbyId, username);
        } catch (Exception e) {
            sendError("GAME_START_FAILED", "Error starting game: " + e.getMessage());
        }
    }

    private void processGameAction(JSONObject data) {
        String action = JsonParser.getString(data, "action", "");
        if (action.isEmpty()) {
            sendError("GAME_ACTION_FAILED", "Action type is required");
            return;
        }

        try {
            server.processGameAction(username, action, data.toString());
        } catch (Exception e) {
            sendError("GAME_ACTION_FAILED", "Error processing game action: " + e.getMessage());
        }
    }

    public synchronized void sendMessage(String type, JSONObject data) {
        if (!running.get() || out == null) {
            return;
        }

        try {
            NetworkMessage message = new NetworkMessage(type, data != null ? data : JsonBuilder.empty());
            out.println(message.toJsonString());
            out.flush();

            if (out.checkError()) {
                System.err.println("Error sending to client: " + username);
                shutdown();
            }
        } catch (Exception e) {
            System.err.println("Failed to send message to client: " + e.getMessage());
        }
    }

    private void sendError(String code, String message) {
        JSONObject error = JsonBuilder.create()
            .put("code", code)
            .put("message", message)
            .build();
        sendMessage("ERROR", error);
    }

    public void sendLobbiesList() {
        System.out.println("CLientHAndler Sending lobbies list");
        JSONArray lobbiesArray = new JSONArray();

        for (GameLobby lobby : server.getGameLobbies().values()) {
            System.out.println("sending  " + lobby.getName() + " lobby: " + lobby.getId());
//            if (lobby.isVisible() || lobby.hasPlayer(username)) {
            JSONObject lobbyInfo = lobby.getLobbyInfo();

            if (lobby.hasPlayer(username)) {
                JSONArray players = new JSONArray();
                for (String playerName : lobby.getPlayers().keySet()) {
                    players.put(playerName);
                }
                lobbyInfo.put("players", players);
            }
            lobbiesArray.put(lobbyInfo);
//            }
        }

        JSONObject data = JsonBuilder.create()
            .put("lobbies", lobbiesArray)
            .build();
        sendMessage("LOBBIES_LIST", data);
    }

    public void sendPlayerList() {
        JSONObject players = server.sendOnlinePlayersList();
        sendMessage("PLAYERS_LIST", players);
    }

    public synchronized void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
    }

    public void updateLastActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() - lastActivityTime > TIMEOUT_MS;
    }

    public String getUsername() {
        return username;
    }

    public boolean isRunning() {
        return running.get() && !clientSocket.isClosed();
    }

    public GameLobby getLobby() {
        return currentLobby;
    }
    public void setCurrentLobby(GameLobby lobby) {
        this.currentLobby = lobby;
    }
}
