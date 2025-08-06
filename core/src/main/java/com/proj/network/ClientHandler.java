package com.proj.network;

import com.badlogic.gdx.Gdx;
import org.json.JSONException;
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

    public ClientHandler(Socket socket, GameServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.lastActivityTime = System.currentTimeMillis();
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Gdx.app.error("ClientHandler", "eror in input or output", e);
        }
    }

    @Override
    public void run() {
        try {
            if (!authenticate()) {
                return;
            }

            String inputLine;
            while (running.get() && (inputLine = in.readLine()) != null) {
                updateLastActivity();
                try {
                    JSONObject jsonInput = new JSONObject(inputLine);
                    String type = jsonInput.getString("type");
                    JSONObject data = jsonInput.getJSONObject("data");

                    processMessage(type, data);
                } catch (JSONException e) {
                    Gdx.app.error("ClientHandler", "error JSON: " + e.getMessage());
                    sendMessage("ERROR", "error format");
                } catch (Exception e) {
                    Gdx.app.error("ClientHandler", "eror massage: " + e.getMessage(), e);
                    sendMessage("ERROR", "error at request");
                }
            }
        } catch (IOException e) {
            Gdx.app.log("ClientHandler", "connection stop", e);
        } finally {
            if (username != null) {
                server.removeClient(username);
            }
            shutdown();
        }
    }

    private boolean authenticate() throws IOException {
        sendMessage("AUTH_REQUEST", "enter name ");

        String response = in.readLine();
        if (response == null) {
            return false;
        }

        try {
            JSONObject authData = new JSONObject(response);

            if (!authData.has("data") || !authData.getJSONObject("data").has("username") ||
                !authData.getJSONObject("data").has("password")) {
                sendMessage("AUTH_FAILED", "problem");
                return false;
            }

            String username = authData.getJSONObject("data").getString("username");
            String password = authData.getJSONObject("data").getString("password");

            boolean authenticated = server.getDatabaseHelper().verifyUser(username, password);

            if (authenticated) {
                if (server.getConnectedClients().containsKey(username)) {
                    ClientHandler existingClient = server.getConnectedClients().get(username);
                    existingClient.sendMessage("DISCONNECT", "your connection another device");
                    existingClient.shutdown();
                }

                this.username = username;
                server.registerClient(username, this);
                sendMessage("AUTH_SUCCESS", "wellcome " + username);
                return true;
            } else {
                sendMessage("AUTH_FAILED", "wrong name or pass");
                return false;
            }
        } catch (JSONException e) {
            sendMessage("AUTH_FAILED", "فرمت اطلاعات احراز هویت نامعتبر است");
            Gdx.app.error("ClientHandler", "خطا در پردازش اطلاعات احراز هویت", e);
            return false;
        }
    }

    private void processMessage(String type, JSONObject data) {
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

            case "GET_LOBBIES":
                sendLobbiesList();
                break;

            case "LEAVE_LOBBY":
                leaveLobby(data);
                break;

            case "PING":
                sendMessage("PONG", "");
                break;

            default:
                Gdx.app.log("ClientHandler", "نوع پیام ناشناخته: " + type);
                sendMessage("ERROR", "نوع پیام نامعتبر است");
                break;
        }
    }

    private void processChatMessage(JSONObject data) {
        if (!data.has("message")) {
            sendMessage("ERROR", "پیام چت خالی است");
            return;
        }

        String message = data.getString("message");
        if (message.trim().isEmpty()) {
            return;
        }

        if (message.length() > 200) {
            message = message.substring(0, 200) + "...";
        }

        if (data.has("recipient")) {
            String recipient = data.getString("recipient");
            ClientHandler recipientHandler = server.getConnectedClients().get(recipient);

            if (recipientHandler != null) {
                JSONObject privateMsg = new JSONObject();
                privateMsg.put("sender", username);
                privateMsg.put("message", message);
                privateMsg.put("isPrivate", true);

                recipientHandler.sendMessage("PRIVATE_CHAT", privateMsg.toString());
                sendMessage("PRIVATE_CHAT", privateMsg.toString()); // ارسال به فرستنده
            } else {
                sendMessage("ERROR", "کاربر " + recipient + " آنلاین نیست");
            }
        } else {
            server.broadcastSystemMessage(username + ": " + message);
        }
    }

    private void createLobby(JSONObject data) {
        try {
            if (!data.has("name") || !data.has("maxPlayers") ||
                !data.has("isPrivate") || !data.has("isVisible")) {
                sendMessage("ERROR", "اطلاعات ایجاد لابی ناقص است");
                return;
            }

            String name = data.getString("name");
            String password = data.optString("password", "");
            int maxPlayers = data.getInt("maxPlayers");
            boolean isPrivate = data.getBoolean("isPrivate");
            boolean isVisible = data.getBoolean("isVisible");

            if (name.trim().isEmpty()) {
                sendMessage("ERROR", "نام لابی نمی‌تواند خالی باشد");
                return;
            }

            if (maxPlayers < 2 || maxPlayers > 8) {
                sendMessage("ERROR", "تعداد بازیکنان باید بین 2 تا 8 باشد");
                return;
            }

            if (isPrivate && password.trim().isEmpty()) {
                sendMessage("ERROR", "لابی خصوصی نیاز به رمز عبور دارد");
                return;
            }

            server.createLobby(name, username, password, maxPlayers, isPrivate, isVisible);

        } catch (Exception e) {
            sendMessage("ERROR", "خطا در ایجاد لابی: " + e.getMessage());
            Gdx.app.error("ClientHandler", "خطا در ایجاد لابی", e);
        }
    }

    private void joinLobby(JSONObject data) {
        try {
            if (!data.has("lobbyId")) {
                sendMessage("ERROR", "شناسه لابی مشخص نشده است");
                return;
            }

            String lobbyId = data.getString("lobbyId");
            String password = data.optString("password", "");

            server.joinLobby(lobbyId, username, password);

        } catch (Exception e) {
            sendMessage("ERROR", "خطا در پیوستن به لابی: " + e.getMessage());
            Gdx.app.error("ClientHandler", "خطا در پیوستن به لابی", e);
        }
    }

    private void leaveLobby(JSONObject data) {
        GameLobby lobby = server.findPlayerLobby(username);
        if (lobby != null) {
            lobby.removePlayer(username);
            sendMessage("SUCCESS", "شما از لابی خارج شدید");

            if (lobby.isEmpty()) {
                server.getGameLobbies().remove(lobby.getId());
                server.broadcastSystemMessage("لابی " + lobby.getName() + " حذف شد");
            }
        } else {
            sendMessage("ERROR", "شما در هیچ لابی‌ای نیستید");
        }
    }

    private void startGame(JSONObject data) {
        try {
            if (!data.has("lobbyId")) {
                sendMessage("ERROR", "شناسه لابی مشخص نشده است");
                return;
            }

            String lobbyId = data.getString("lobbyId");
            server.startGame(lobbyId, username);

        } catch (Exception e) {
            sendMessage("ERROR", "خطا در شروع بازی: " + e.getMessage());
            Gdx.app.error("ClientHandler", "خطا در شروع بازی", e);
        }
    }

    private void processGameAction(JSONObject data) {
        try {
            if (!data.has("action")) {
                sendMessage("ERROR", "نوع اکشن مشخص نشده است");
                return;
            }

            String action = data.getString("action");
            server.processGameAction(username, action, data.toString());

        } catch (Exception e) {
            sendMessage("ERROR", "خطا در پردازش اکشن بازی: " + e.getMessage());
            Gdx.app.error("ClientHandler", "خطا در پردازش اکشن بازی", e);
        }
    }

    public synchronized void sendMessage(String type, String data) {
        if (!running.get() || out == null) {
            return;
        }

        try {
            JSONObject response = new JSONObject();
            response.put("type", type);
            response.put("data", data);
            out.println(response.toString());

            if (out.checkError()) {
                Gdx.app.error("ClientHandler", "خطا در ارسال پیام به کلاینت: " + username);
                shutdown();
            }
        } catch (Exception e) {
            Gdx.app.error("ClientHandler", "خطا در ساخت پیام JSON", e);
        }
    }

    private void sendLobbiesList() {
        JSONObject lobbiesData = new JSONObject();

        for (GameLobby lobby : server.getGameLobbies().values()) {
            if (lobby.isVisible() || lobby.hasPlayer(username)) {
                JSONObject lobbyInfo = new JSONObject();
                lobbyInfo.put("id", lobby.getId());
                lobbyInfo.put("name", lobby.getName());
                lobbyInfo.put("owner", lobby.getOwner());
                lobbyInfo.put("playerCount", lobby.getPlayerCount());
                lobbyInfo.put("maxPlayers", lobby.getMaxPlayers());
                lobbyInfo.put("isPrivate", lobby.isPrivate());
                lobbyInfo.put("isGameActive", lobby.isGameActive());

                if (lobby.hasPlayer(username)) {
                    JSONObject players = new JSONObject();
                    for (String playerName : lobby.getPlayers().keySet()) {
                        players.put(playerName, true);
                    }
                    lobbyInfo.put("players", players);
                }

                lobbiesData.put(lobby.getId(), lobbyInfo);
            }
        }

        sendMessage("LOBBIES_LIST", lobbiesData.toString());
    }

    public synchronized void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                Gdx.app.error("ClientHandler", "خطا در بستن اتصال کلاینت", e);
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
}
