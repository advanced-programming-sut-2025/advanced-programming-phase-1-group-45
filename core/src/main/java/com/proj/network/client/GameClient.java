package com.proj.network.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.proj.network.event.GameEvent;
import com.proj.network.event.LobbyEvent;
import com.proj.network.event.NetworkEvent;
import com.proj.network.message.AuthRequest;
import com.proj.network.message.JsonBuilder;
import com.proj.network.message.JsonParser;
import com.proj.network.message.NetworkMessage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameClient implements Disposable, Runnable {
    private static final long PING_INTERVAL = 15000; // 15 seconds

    private final String serverAddress;
    private final int serverPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread networkThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<NetworkEventListener> listeners = new ArrayList<>();
    List<LobbyEventListener> lobbyListeners = new ArrayList<>();
    List<LobbyListListener> lobbyListListeners = new ArrayList<>();

    private String username;
    private String currentLobbyId;
    private boolean authenticated = false;

    public GameClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() {
        if (running.get()) return;

        try {
            clientSocket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            running.set(true);
            if (networkThread == null || !networkThread.isAlive()) {
                networkThread = new Thread(this, "Network-Thread");
                networkThread.start();
            }

            startPingTask();
            fireEvent(NetworkEvent.Type.CONNECTED, "Connected to server");
        } catch (IOException e) {
            fireEvent(NetworkEvent.Type.ERROR, "Connection error: " + e.getMessage());
            Gdx.app.error("GameClient", "Connection error", e);
        }
    }

    public void addLobbyEventListener(LobbyEventListener listener) {
        if (!lobbyListeners.contains(listener)) {
            lobbyListeners.add(listener);
        }
    }

    public void addLobbyListListener(LobbyListListener listener) {
        if (!lobbyListListeners.contains(listener)) {
            lobbyListListeners.add(listener);
        }
    }

    public void removeLobbyEventListener(LobbyEventListener listener) {
        lobbyListeners.remove(listener);
    }

    public void removeLobbyListListener(LobbyListListener listener) {
        lobbyListListeners.remove(listener);
    }

    public void removeNetworkEventListener(NetworkEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                String message = in.readLine();
                if (message == null) break;
                processIncomingMessage(message);
            }
        } catch (IOException e) {
            if (running.get()) {
                fireEvent(NetworkEvent.Type.ERROR, "Network error: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    private void processIncomingMessage(String rawMessage) {
        try {
            // Use the NetworkMessage factory for safe parsing
            NetworkMessage message = NetworkMessage.parse(rawMessage);
            dispatchMessage(message);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Invalid message format: " + e.getMessage());
        }
    }

    private void dispatchMessage(NetworkMessage message) {
        try {
            String type = message.getType();
            JSONObject data = message.getData();

            switch (type) {
                case "AUTH_REQUEST":
                    fireEvent(NetworkEvent.Type.AUTH_REQUEST, data.toString());
                    break;

                case "AUTH_SUCCESS":
                    handleAuthSuccess(data);
                    break;

                case "AUTH_FAILED":
                    fireEvent(NetworkEvent.Type.AUTH_FAILED,
                        JsonParser.getString(data, "message", "Authentication failed"));
                    break;

                case "LOBBY_CREATED":
                    handleLobbyCreated(data);
                    break;

                case "LOBBY_UPDATE" :
                    handleLobbyUpdate(data);
                    break;


                case "JOIN_SUCCESS":
                    handleJoinLobby(data);
                    break;
                case "LOBBY_ADDED" :
                    handleLobbyAdded(data);
                    break;
                case "LEAVE_SUCCESS":
                    handleLeaveLobby();
                    break;

                case "LOBBIES_LIST":
                    fireLobbyListEvent(data);
                    break;

                    case "PLAYERS_LIST":
                        handleOnlinePlayersResponse(data);
                        break;


                case "GAME_STARTED":
                    fireLobbyEvent(LobbyEvent.Type.GAME_STARTED, data);
                    break;

                case "GAME_UPDATE":
                    fireGameEvent(GameEvent.Type.UPDATE, data);
                    break;

                case "PRIVATE_CHAT":
                    handlePrivateChat(data);
                    break;

                case "SYSTEM":
                    fireEvent(NetworkEvent.Type.SYSTEM_MESSAGE,
                        JsonParser.getString(data, "message", "System message"));
                    break;

                case "PONG":
                    // Ping response - no action needed
                    break;

                case "ERROR":
                    handleError(data);
                    break;

                default:
                    fireEvent(NetworkEvent.Type.UNKNOWN_MESSAGE, message.toString());
                    break;
            }
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error handling message: " + e.getMessage());
        }
    }

    private void handleAuthSuccess(JSONObject data) {
        try {
            this.username = JsonParser.getString(data, "username", "");
            if (!username.isEmpty()) {
                this.authenticated = true;
                fireEvent(NetworkEvent.Type.AUTH_SUCCESS, "Welcome " + username);
            } else {
                fireEvent(NetworkEvent.Type.ERROR, "Authentication succeeded but no username provided");
            }
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Invalid auth success data: " + e.getMessage());
        }
    }

    private void handleLobbyUpdate(JSONObject data) {
        fireLobbyEvent(LobbyEvent.Type.LOBBY_UPDATE, data);
    }

    private void handleNewJoinToLobby(JSONObject data) {
        fireLobbyEvent(LobbyEvent.Type.LOBBY_UPDATE, data);
    }

    private void handleLobbyCreated(JSONObject data) {
        try {
            currentLobbyId = JsonParser.getString(data, "id", "");
            fireLobbyEvent(LobbyEvent.Type.LOBBY_CREATED, data);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error processing lobby creation: " + e.getMessage());
        }
    }


    public void requestOnlinePlayers() {
        sendMessage("GET_ONLINE_PLAYERS", new JSONObject());
    }

    public void handleOnlinePlayersResponse(JSONObject data) {
        for (LobbyEventListener listener : lobbyListeners) {
            listener.handleLobbyEvent(
                new LobbyEvent(LobbyEvent.Type.ONLINE_PLAYERS_RECEIVED,
                    data.toString())
            );
        }
    }

    private void handleLobbyAdded(JSONObject data) {
        try {
            fireLobbyEvent(LobbyEvent.Type.LOBBY_ADDED, data);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error processing lobby added: " + e.getMessage());
        }
    }

    private void handleJoinLobby(JSONObject data) {
        try {
            currentLobbyId = JsonParser.getString(data, "id", "");
            fireLobbyEvent(LobbyEvent.Type.JOIN_SUCCESS, data);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error joining lobby: " + e.getMessage());
        }
    }

    private void handleLeaveLobby() {
        currentLobbyId = null;
        fireLobbyEvent(LobbyEvent.Type.LEFT, JsonBuilder.empty());
    }

    private void handlePrivateChat(JSONObject data) {
        try {
            String sender = JsonParser.getString(data, "sender", "Unknown");
            String message = JsonParser.getString(data, "message", "");
            fireEvent(NetworkEvent.Type.PRIVATE_MESSAGE, sender + ": " + message);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error processing private chat: " + e.getMessage());
        }
    }

    private void handleError(JSONObject data) {
        try {
            String code = JsonParser.getString(data, "code", "UNKNOWN_ERROR");
            String message = JsonParser.getString(data, "message", "An error occurred");
            fireEvent(NetworkEvent.Type.ERROR, "[" + code + "] " + message);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error processing error message: " + e.getMessage());
        }
    }

    public void login(String username, String password) {
        AuthRequest request = new AuthRequest(username, password);
        sendMessage("AUTH", request.toJson());
    }

    public void startGame(String lobbyId) {
        JSONObject data = JsonBuilder.create()
            .put("lobbyId", lobbyId)
            .build();
        sendMessage("START_GAME", data);
    }

    public void createLobby(String name, String password, int maxPlayers,
                            boolean isPrivate, boolean isVisible) {
        JSONObject data = JsonBuilder.create()
            .put("name", name)
            .put("password", password)
            .put("maxPlayers", maxPlayers)
            .put("isPrivate", isPrivate)
            .put("isVisible", isVisible)
            .build();

        sendMessage("CREATE_LOBBY", data);
    }

    public void joinLobby(String lobbyId, String password) {
        JSONObject data = JsonBuilder.create()
            .put("lobbyId", lobbyId)
            .put("password", password)
            .put("password", password)
            .build();

        sendMessage("JOIN_LOBBY", data);
    }

    public void leaveLobby() {
        sendMessage("LEAVE_LOBBY", JsonBuilder.empty());
    }

    public void sendChatMessage(String message, boolean isPrivate, String recipient) {
        JSONObject data = JsonBuilder.create()
            .put("message", message)
            .putIf(isPrivate && recipient != null, "recipient", recipient)
            .build();

        sendMessage("CHAT", data);
    }

    public void requestLobbiesList() {
        sendMessage("GET_LOBBIES", JsonBuilder.empty());
    }

    public void sendGameAction(String actionType, JSONObject actionData) {
        JSONObject data = JsonBuilder.create()
            .put("action", actionType)
            .put("data", actionData)
            .build();

        sendMessage("GAME_ACTION", data);
    }

    private void sendMessage(String type, JSONObject data) {
        if (!running.get() || out == null) return;

        try {
            NetworkMessage message = new NetworkMessage(type, data != null ? data : JsonBuilder.empty());
            out.println(message.toJsonString());
            out.flush();
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "Error sending message: " + e.getMessage());
        }
    }

    private void startPingTask() {
        new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(PING_INTERVAL);
                    sendMessage("PING", JsonBuilder.empty());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Ping-Thread").start();
    }

    public void disconnect() {
        if (running.compareAndSet(true, false)) {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                Gdx.app.error("GameClient", "Error closing connection", e);
            }

            authenticated = false;
            username = null;
            currentLobbyId = null;
            fireEvent(NetworkEvent.Type.DISCONNECTED, "Disconnected from server");
        }
    }

    @Override
    public void dispose() {
        disconnect();
    }

    // Event management
    public void addNetworkListener(NetworkEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeNetworkListener(NetworkEventListener listener) {
        listeners.remove(listener);
    }

    private void fireEvent(NetworkEvent.Type type, String message) {
        NetworkEvent event = new NetworkEvent(type, message);
        for (NetworkEventListener listener : listeners) {
            listener.handleNetworkEvent(event);
        }
    }

    private void fireLobbyEvent(LobbyEvent.Type type, JSONObject data) {
        LobbyEvent event = new LobbyEvent(type, data.toString());
        for (NetworkEventListener listener : lobbyListeners) {
            if (listener instanceof LobbyEventListener) {
                ((LobbyEventListener) listener).handleLobbyEvent(event);
            }
        }
    }

    private void fireGameEvent(GameEvent.Type type, JSONObject data) {
        GameEvent event = new GameEvent(type, data.toString());
        for (NetworkEventListener listener : listeners) {
            if (listener instanceof GameEventListener) {
                ((GameEventListener) listener).handleGameEvent(event);
            }
        }
    }

    private void fireLobbyListEvent(JSONObject data) {
        for (LobbyListListener listener : lobbyListListeners) {
            (listener).onLobbiesReceived(data);
        }
    }

    public boolean isConnected() {
        return running.get();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentLobbyId() {
        return currentLobbyId;
    }
}
