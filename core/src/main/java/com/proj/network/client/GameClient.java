package com.proj.network.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.proj.network.event.GameEvent;
import com.proj.network.event.LobbyEvent;
import com.proj.network.event.NetworkEvent;
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
    private static final long PING_INTERVAL = 15000; // 15 ثانیه

    private final String serverAddress;
    private final int serverPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread networkThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<NetworkEventListener> listeners = new ArrayList<>();

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
            networkThread = new Thread(this, "Network-Thread");
            networkThread.start();

            startPingTask();
            fireEvent(NetworkEvent.Type.CONNECTED, "connected");
        } catch (IOException e) {
            fireEvent(NetworkEvent.Type.ERROR, "connection error: " + e.getMessage());
            Gdx.app.error("GameClient", "Connection error", e);
        }
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
                fireEvent(NetworkEvent.Type.ERROR, "network error: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    private void processIncomingMessage(String rawMessage) {
        try {
            JSONObject json = new JSONObject(rawMessage);
            String type = json.getString("type");
            String data = json.optString("data", "");

            switch (type) {
                case "AUTH_REQUEST":
                    fireEvent(NetworkEvent.Type.AUTH_REQUEST, data);
                    break;
                case "AUTH_SUCCESS":
                    handleAuthSuccess(data);
                    break;

                case "AUTH_FAILED":
                    fireEvent(NetworkEvent.Type.AUTH_FAILED, data);
                    break;

                case "LOBBY_CREATED":
                    handleLobbyCreated(data);
                    break;

                case "JOIN_SUCCESS":
                    handleJoinLobby(data);
                    break;

                case "LEAVE_SUCCESS" :
                    handleLeaveLobby(data);
                    break;

                case "LOBBIES_LIST":
                    fireLobbyListEvent(data);
                    break;

                case "GAME_STARTED":
                    fireGameEvent(GameEvent.Type.STARTED, data);
                    break;

                case "GAME_UPDATE":
                    fireGameEvent(GameEvent.Type.UPDATE, data);
                    break;

                case "PRIVATE_CHAT":
                    handlePrivateChat(data);
                    break;

                case "SYSTEM":
                    fireEvent(NetworkEvent.Type.SYSTEM_MESSAGE, data);
                    break;

                case "PONG":
                    // پاسخ پینگ
                    break;

                default:
                    fireEvent(NetworkEvent.Type.UNKNOWN_MESSAGE, rawMessage);
                    break;
            }
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in processing message: " + e.getMessage());
        }
    }

    private void handleAuthSuccess(String data) {
        authenticated = true;
        username = data.split(" ")[1]; // "خوش آمدید username"
        fireEvent(NetworkEvent.Type.AUTH_SUCCESS, "successful" + username);
    }

    private void handleLobbyCreated(String jsonData) {
        try {
            JSONObject lobbyInfo = new JSONObject(jsonData);
            currentLobbyId = lobbyInfo.getString("id");
            fireLobbyEvent(LobbyEvent.Type.LOBBY_CREATED, jsonData);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in processing lobby: " + e.getMessage());
        }
    }

    private void handleLeaveLobby(String jsonData) {
        try {
            JSONObject lobbyInfo = new JSONObject(jsonData);
            fireLobbyEvent(LobbyEvent.Type.LEFT, null);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in processing lobby: " + e.getMessage());
        }
    }

    private void handleJoinLobby(String jsonData) {
        try {
            JSONObject lobbyInfo = new JSONObject(jsonData);
            currentLobbyId = lobbyInfo.getString("id");
            fireLobbyEvent(LobbyEvent.Type.JOIN_SUCCESS, jsonData);
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in join lobby: " + e.getMessage());
        }
    }

    private void handlePrivateChat(String jsonData) {
        try {
            JSONObject chatData = new JSONObject(jsonData);
            fireEvent(NetworkEvent.Type.PRIVATE_MESSAGE,
                chatData.getString("sender") + ": " + chatData.getString("message"));
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in processing chat: " + e.getMessage());
        }
    }

    public void authenticate(String username, String password) {
        JSONObject authData = new JSONObject();
        authData.put("username", username);
        authData.put("password", password);
        sendMessage("AUTH", authData.toString());
    }

    public void startGame(String lobbyId) {
        JSONObject gameData = new JSONObject();
        gameData.put("lobbyId", lobbyId);
        sendMessage("START_GAME", gameData.toString());
    }

    public void createLobby(String name, String password, int maxPlayers, boolean isPrivate, boolean isVisible) {
        JSONObject lobbyData = new JSONObject();
        lobbyData.put("name", name);
        lobbyData.put("password", password);
        lobbyData.put("maxPlayers", maxPlayers);
        lobbyData.put("isPrivate", isPrivate);
        lobbyData.put("isVisible", isVisible);
        sendMessage("CREATE_LOBBY", lobbyData.toString());
    }

    public void joinLobby(String lobbyId, String password) {
        JSONObject joinData = new JSONObject();
        joinData.put("lobbyId", lobbyId);
        joinData.put("password", password);
        sendMessage("JOIN_LOBBY", joinData.toString());
    }

    public void leaveLobby() {
        sendMessage("LEAVE_LOBBY", "");
        currentLobbyId = null;
    }

    public void sendChatMessage(String message, boolean isPrivate, String recipient) {
        JSONObject chatData = new JSONObject();
        chatData.put("message", message);
        if (isPrivate && recipient != null) {
            chatData.put("recipient", recipient);
        }
        sendMessage("CHAT", chatData.toString());
    }

    public void requestLobbiesList() {
        sendMessage("GET_LOBBIES", "");
    }

    public void sendGameAction(String actionType, JSONObject actionData) {
        JSONObject gameAction = new JSONObject();
        gameAction.put("action", actionType);
        gameAction.put("data", actionData);
        sendMessage("GAME_ACTION", gameAction.toString());
    }

    private void sendMessage(String type, String data) {
        if (!running.get() || out == null) return;

        try {
            JSONObject message = new JSONObject();
            message.put("type", type);
            message.put("data", data);
            out.println(message.toString());
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in sending message");
        }
    }

    private void startPingTask() {
        new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(PING_INTERVAL);
                    sendMessage("PING", "");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Ping-Thread").start();
    }

    public void disconnect() {
        running.set(false);
        authenticated = false;
        username = null;
        currentLobbyId = null;

        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            Gdx.app.error("GameClient", "Error closing connection", e);
        }

        fireEvent(NetworkEvent.Type.DISCONNECTED, "connection disconnected");
    }

    @Override
    public void dispose() {
        disconnect();
    }

    // مدیریت رویدادها
    public void addNetworkListener(NetworkEventListener listener) {
        listeners.add(listener);
    }

    public void addLobbyListener(LobbyEventListener listener) {
        listeners.add(listener);
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

    private void fireLobbyEvent(LobbyEvent.Type type, String data) {
        LobbyEvent event = new LobbyEvent(type, data);
        for (NetworkEventListener listener : listeners) {
            if (listener instanceof LobbyEventListener) {
                ((LobbyEventListener) listener).handleLobbyEvent(event);
            }
        }
    }

    private void fireGameEvent(GameEvent.Type type, String data) {
        GameEvent event = new GameEvent(type, data);
        for (NetworkEventListener listener : listeners) {
            if (listener instanceof GameEventListener) {
                ((GameEventListener) listener).handleGameEvent(event);
            }
        }
    }

    private void fireLobbyListEvent(String jsonData) {
        try {
            JSONObject lobbiesData = new JSONObject(jsonData);
            for (NetworkEventListener listener : listeners) {
                if (listener instanceof LobbyListListener) {
                    ((LobbyListListener) listener).onLobbiesReceived(lobbiesData);
                }
            }
        } catch (Exception e) {
            fireEvent(NetworkEvent.Type.ERROR, "error in receiving lobbies");
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
