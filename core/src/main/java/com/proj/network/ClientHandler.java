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
            System.err.println("ClientHandler  " + "eror in input or output" + e);
        }
    }

    @Override
    public void run() {
        try {
            if (!signUp()) {
                return;
            }

            String inputLine;
            while (running.get() && (inputLine = in.readLine()) != null) {
                updateLastActivity();
                try {
                    JSONObject jsonInput = new JSONObject(inputLine);
                    String type = jsonInput.getString("type");
                    Object data = jsonInput.get("data");
                    if(data instanceof JSONObject) {
                        processMessage(type, (JSONObject) data);
                    } else if (data instanceof String) {
                        JSONObject dataInput = new JSONObject((String)data);
                        processMessage(type, dataInput);
                    }

                } catch (JSONException e) {
                    System.err.println("ClientHandler  " + "error JSON: " + e.getMessage());
                    sendMessage("ERROR", "error format");
                } catch (Exception e) {
                    System.err.println("ClientHandler  " + "error massage: " + e.getMessage() + e);
                    sendMessage("ERROR", "error at request");
                }
            }
        } catch (IOException e) {
            System.err.println("ClientHandler " + "connection stop" + e);
        } finally {
            if (username != null) {
                server.removeClient(username);
            }
            shutdown();
        }
    }

    private boolean signUp() throws IOException {
        sendMessage("AUTH_REQUEST", "Please enter your credentials");

        String response = in.readLine();
        if (response == null) {
            return false;
        }

        try {
            JSONObject authData = new JSONObject(response);

            // Handle both string and object formats for "data"
            Object dataObj = authData.get("data");
            JSONObject credentials;

            if (dataObj instanceof String) {
                // Parse the string as JSON object
                credentials = new JSONObject((String) dataObj);
            } else if (dataObj instanceof JSONObject) {
                credentials = (JSONObject) dataObj;
            } else {
                sendMessage("AUTH_FAILED", "Invalid credentials format");
                return false;
            }

            // Validate required fields
            if (!credentials.has("username") || !credentials.has("password")) {
                sendMessage("AUTH_FAILED", "Missing username or password");
                return false;
            }

            String username = credentials.getString("username");
            String password = credentials.getString("password");
            String securityQuestion = credentials.getString("securityQuestion");

            boolean authenticated = server.getDatabaseHelper().addUser(username, password, securityQuestion);

            if (authenticated) {
                // Handle existing connections
                if (server.getConnectedClients().containsKey(username)) {
                    ClientHandler existingClient = server.getConnectedClients().get(username);
                    existingClient.sendMessage("DISCONNECT", "You've been disconnected because you logged in from another device");
                    existingClient.shutdown();
                }
                this.username = username;
                server.registerClient(username, this);
                sendMessage("AUTH_SUCCESS", "Welcome " + username);
                return true;
            } else {
                sendMessage("AUTH_FAILED", "Invalid format");
                return false;
            }
        } catch (JSONException e) {
            sendMessage("AUTH_FAILED", "Invalid authentication format");
            System.err.println("ClientHandler error in authenticate: " + e.getMessage());
            return false;
        }
    }

//    private boolean authenticate() throws IOException {
//        sendMessage("AUTH_REQUEST", "Please enter your credentials");
//
//        String response = in.readLine();
//        if (response == null) {
//            return false;
//        }
//
//        try {
//            JSONObject authData = new JSONObject(response);
//
//            // Handle both string and object formats for "data"
//            Object dataObj = authData.get("data");
//            JSONObject credentials;
//
//            if (dataObj instanceof String) {
//                // Parse the string as JSON object
//                credentials = new JSONObject((String) dataObj);
//            } else if (dataObj instanceof JSONObject) {
//                credentials = (JSONObject) dataObj;
//            } else {
//                sendMessage("AUTH_FAILED", "Invalid credentials format");
//                return false;
//            }
//
//            // Validate required fields
//            if (!credentials.has("username") || !credentials.has("password")) {
//                sendMessage("AUTH_FAILED", "Missing username or password");
//                return false;
//            }
//
//            String username = credentials.getString("username");
//            String password = credentials.getString("password");
//
//            boolean authenticated = server.getDatabaseHelper().verifyUser(username, password);
//
//            if (authenticated) {
//                // Handle existing connections
//                if (server.getConnectedClients().containsKey(username)) {
//                    ClientHandler existingClient = server.getConnectedClients().get(username);
//                    existingClient.sendMessage("DISCONNECT", "You've been disconnected because you logged in from another device");
//                    existingClient.shutdown();
//                }
//
//                this.username = username;
//                server.registerClient(username, this);
//                sendMessage("AUTH_SUCCESS", "Welcome " + username);
//                return true;
//            } else {
//                sendMessage("AUTH_FAILED", "Invalid username or password");
//                return false;
//            }
//        } catch (JSONException e) {
//            sendMessage("AUTH_FAILED", "Invalid authentication format");
//            System.err.println("ClientHandler error in authenticate: " + e.getMessage());
//            return false;
//        }
//    }

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
                sendMessage("ERROR", "Lobby creation information are incomplete");
                System.err.println("GAme Client " +  "Creating a new lobby: " + data.toString());

                System.err.println("ClientHandler  information Are bad");

                return;
            }

            String name = data.getString("name");
            String password = data.optString("password", "");
            int maxPlayers = data.getInt("maxPlayers");
            boolean isPrivate = data.getBoolean("isPrivate");
            boolean isVisible = data.getBoolean("isVisible");

            System.err.println("ClientHandler  information Are good");

            if (name.trim().isEmpty()) {
                sendMessage("ERROR", "Lobby name must not be empty");
                return;
            }

            if (maxPlayers < 2 || maxPlayers > 8) {
                sendMessage("ERROR", "Players number should be between 2 and 8");
                return;
            }

            if (isPrivate && password.trim().isEmpty()) {
                sendMessage("ERROR", "Private Lobby should have a password");
                return;
            }

            server.createLobby(name, username, password, maxPlayers, isPrivate, isVisible);

            System.out.println("createLobby");

        } catch (Exception e) {
            sendMessage("ERROR", "error in creating lobby: " + e.getMessage());
            System.err.println("ClientHandler  " + "error in create lobby: " + e);
        }
    }

    private void joinLobby(JSONObject data) {
        try {
            if (!data.has("lobbyId")) {
                sendMessage("ERROR", "Should send lobby ID in the message");
                return;
            }

            String lobbyId = data.getString("lobbyId");
            String password = data.optString("password", "");

            server.joinLobby(lobbyId, username, password);

        } catch (Exception e) {
            sendMessage("ERROR", "error in join lobby: " + e.getMessage());
            System.err.println("ClientHandler  " + "error in join lobby" + e);
        }
    }

    private void leaveLobby(JSONObject data) {
        GameLobby lobby = server.findPlayerLobby(username);
        if (lobby != null) {
            lobby.removePlayer(username);
            sendMessage("LEAVE_SUCCESS", "You left the lobby");

            if (lobby.isEmpty()) {
                server.getGameLobbies().remove(lobby.getId());
                server.broadcastSystemMessage("Lobby: " + lobby.getName() + " was removed");
            }
        } else {
            sendMessage("ERROR", "You are not in any lobby");
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
            System.err.println("ClientHandler  " + "خطا در شروع بازی" + e);
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
            System.err.println("ClientHandler  " + "خطا در پردازش اکشن بازی" + e);
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
                System.err.println("ClientHandler  " + "خطا در ارسال پیام به کلاینت: " + username);
                shutdown();
            }
        } catch (Exception e) {
            System.err.println("ClientHandler  " + "خطا در ساخت پیام JSON" + e);
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
                System.err.println("ClientHandler  " + "خطا در بستن اتصال کلاینت" + e);
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
