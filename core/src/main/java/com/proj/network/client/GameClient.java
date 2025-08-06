package com.proj.network.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameClient implements Disposable {
    private static final String TAG = "GameClient";
    private static final long RECONNECT_DELAY = 5000;

    private final String serverAddress;
    private final int serverPort;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread connectionThread;
    private Thread listenerThread;

    private ClientListener listener;

    public GameClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() {
        if (running.get()) {
            Gdx.app.log(TAG, "Client is already running");
            return;
        }

        running.set(true);
        connectionThread = new Thread(this::connectionHandler);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private void connectionHandler() {
        while (running.get()) {
            try {
                socket = new Socket(serverAddress, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connected.set(true);
                Gdx.app.log(TAG, "Connected to server");

                if (listener != null) {
                    Gdx.app.postRunnable(listener::onConnected);
                }

                startListening();
                return;

            } catch (IOException e) {
                Gdx.app.error(TAG, "Connection failed: " + e.getMessage());
                connected.set(false);

                if (listener != null) {
                    Gdx.app.postRunnable(listener::onConnectionFailed);
                }

                try {
                    Thread.sleep(RECONNECT_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void startListening() {
        listenerThread = new Thread(this::messageListener);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void messageListener() {
        try {
            String message;
            while (connected.get() && (message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            Gdx.app.error(TAG, "Error reading from server: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void processMessage(String jsonMessage) {
        try {
            JSONObject message = new JSONObject(jsonMessage);
            String type = message.getString("type");
            String data = message.optString("data", "");

            if (listener == null) return;

            switch (type) {
                case "AUTH_REQUEST":
                    listener.onAuthRequest();
                    break;

                case "AUTH_SUCCESS":
                    listener.onAuthSuccess(data);
                    break;

                case "AUTH_FAILED":
                    listener.onAuthFailed(data);
                    break;

                case "LOBBIES_LIST":
                    listener.onLobbiesListReceived(data);
                    break;

                case "LOBBY_CREATED":
                    listener.onLobbyCreated(data);
                    break;

                case "JOIN_SUCCESS":
                    listener.onJoinLobbySuccess(data);
                    break;

                case "GAME_STARTED":
                    listener.onGameStarted(data);
                    break;

                case "CHAT":
                    listener.onChatMessage(data);
                    break;

                case "PRIVATE_CHAT":
                    listener.onPrivateChatMessage(data);
                    break;

                case "SYSTEM":
                    listener.onSystemMessage(data);
                    break;

                case "ERROR":
                    listener.onErrorMessage(data);
                    break;

                case "DISCONNECT":
                    listener.onDisconnectRequest(data);
                    disconnect();
                    break;

                default:
                    Gdx.app.log(TAG, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            Gdx.app.error(TAG, "Error processing message: " + e.getMessage());
        }
    }

    public void sendAuth(String username, String password) {
        JSONObject authData = new JSONObject();
        authData.put("username", username);
        authData.put("password", password);

        JSONObject message = new JSONObject();
        message.put("type", "AUTH");
        message.put("data", authData);

        sendMessage(message.toString());
    }

    public void sendCreateLobby(String name, int maxPlayers, boolean isPrivate, String password) {
        JSONObject lobbyData = new JSONObject();
        lobbyData.put("name", name);
        lobbyData.put("maxPlayers", maxPlayers);
        lobbyData.put("isPrivate", isPrivate);
        lobbyData.put("password", password);
        lobbyData.put("isVisible", true);

        JSONObject message = new JSONObject();
        message.put("type", "CREATE_LOBBY");
        message.put("data", lobbyData);

        sendMessage(message.toString());
    }

    public void sendJoinLobby(String lobbyId, String password) {
        JSONObject joinData = new JSONObject();
        joinData.put("lobbyId", lobbyId);
        joinData.put("password", password);

        JSONObject message = new JSONObject();
        message.put("type", "JOIN_LOBBY");
        message.put("data", joinData);

        sendMessage(message.toString());
    }

    public void sendLeaveLobby() {
        JSONObject message = new JSONObject();
        message.put("type", "LEAVE_LOBBY");
        message.put("data", new JSONObject());

        sendMessage(message.toString());
    }

    public void sendStartGame() {
        JSONObject message = new JSONObject();
        message.put("type", "START_GAME");
        message.put("data", new JSONObject());

        sendMessage(message.toString());
    }

    public void sendChatMessage(String message, boolean isPrivate, String recipient) {
        JSONObject chatData = new JSONObject();
        chatData.put("message", message);

        if (isPrivate && recipient != null) {
            chatData.put("recipient", recipient);
        }

        JSONObject json = new JSONObject();
        json.put("type", "CHAT");
        json.put("data", chatData);

        sendMessage(json.toString());
    }

    public void requestLobbiesList() {
        JSONObject message = new JSONObject();
        message.put("type", "GET_LOBBIES");
        message.put("data", new JSONObject());

        sendMessage(message.toString());
    }

    public void sendPing() {
        JSONObject message = new JSONObject();
        message.put("type", "PING");
        message.put("data", new JSONObject());

        sendMessage(message.toString());
    }

    private synchronized void sendMessage(String message) {
        if (!connected.get() || out == null) {
            Gdx.app.error(TAG, "Cannot send message - not connected to server");
            return;
        }

        out.println(message);
        if (out.checkError()) {
            Gdx.app.error(TAG, "Error sending message");
            disconnect();
        }
    }

    public void disconnect() {
        connected.set(false);
        running.set(false);

        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            Gdx.app.error(TAG, "Error closing connection: " + e.getMessage());
        }

        if (listener != null) {
            Gdx.app.postRunnable(listener::onDisconnected);
        }

        Gdx.app.log(TAG, "Disconnected from server");
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    @Override
    public void dispose() {
        disconnect();
    }
}
