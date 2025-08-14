package com.proj.network;

import com.badlogic.gdx.Gdx;
import com.proj.Model.TimeAndWeather.Weather;
import com.proj.map.farmName;
import com.proj.network.lobby.GameLobby;
import com.proj.network.message.JsonBuilder;
import com.proj.network.message.JsonParser;
import com.proj.network.message.Command;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnectionController implements Runnable {
    private final Socket clientSocket;
    private final GameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String username = null;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private long lastActivityTime;
    private static final long TIMEOUT_MS = 60000; 
    private GameLobby currentLobby;

    public ClientConnectionController(Socket socket, GameServer server) {
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
            String command;
            while (running.get() && (command = in.readLine()) != null) {
                updateLastActivity();
                try {
                    Command message = Command.parse(command);
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
        String response = in.readLine();
        if (response == null) {
            return false;
        }
        try {
            Command loginMessage = Command.parse(response);
            if (!"AUTH".equals(loginMessage.getType())) {
                sendError("AUTH_FAILED", "Invalid request type");
                return false;
            }
            JSONObject credentials = loginMessage.getData();
            String username = JsonParser.getString(credentials, "username", "");
            String password = JsonParser.getString(credentials, "password", "");
            if (username.isEmpty() || password.isEmpty()) {
                System.err.println("Login failed " + "invalid username or password");
                return false;
            }
            if (server.getConnectedClients().containsKey(username)) {
                ClientConnectionController existingClient = server.getConnectedClients().get(username);
                existingClient.sendMessage("DISCONNECT", JsonBuilder.create()
                    .put("message", "You've been disconnected because you logged in from another device")
                    .build());
                existingClient.shutdown();
            }
            this.username = username;
            server.loginClient(username, this);
            sendMessage("LOGIN_SUCCESSFUL", JsonBuilder.create()
                .put("message", "Welcome " + username)
                .put("username", username)
                .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void processMessage(Command message) {
        String type = message.getType();
        JSONObject data = message.getData();
        switch (type) {
            case "CHAT":
                processChatMessage(data);
                break;

            case "CREATE_LOBBY":
                newLobby(data);
                break;

            case "JOIN_LOBBY":
                processJoinLobby(data);
                break;


            case "START":
                playing(data);
                break;

            case "START_GAME":
                startGame(data);
                break;

            case "STATE_TO_START_GAME":
                receiveStateToStart(data);
                break;

            case "GAME_TIME":
                syncGameTime(data);
                break;

            case "READY_TO_PLAY":
                readyToStart(data);
                break;
            case "CHANGE_WEATHER":
                changeGameWeather(data);
                break;

            case "CHANGE_HOUR" :
                changeHour(data);
                break;

            case  "CHANGE_DAY":
                changeDay(data);
                break;

            case "UPDATE_TIME_IN_GAME":
                updateTimeInGame(data);
                break;

            case "GET_ONLINE_PLAYERS":
                sendPlayerList();
                break;

            case "PLAYER_POSITIONS":
                sendPlayerPositions(data);
                break;

                case "MOVE":
                handlePlayerMovement(data);
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
                Gdx.app.log("ClientHandler ", "Unknown message type: " + type);
                break;
        }
    }

    private void processChatMessage(JSONObject data) {
        String messageText = JsonParser.getString(data, "message", "");
        if (messageText.trim().isEmpty()) {
            return;
        }

        if (messageText.length() > 200) {
            messageText = messageText.substring(0, 200) + "...";
        }

        String recipient = JsonParser.getString(data, "recipient", null);
        if (recipient != null) {
            ClientConnectionController recipientHandler = server.getConnectedClients().get(recipient);
            if (recipientHandler != null) {
                JSONObject privateMsg = JsonBuilder.create()
                    .put("sender", username)
                    .put("message", messageText)
                    .put("isPrivate", true)
                    .build();

                recipientHandler.sendMessage("PRIVATE_CHAT", privateMsg);
                sendMessage("PRIVATE_CHAT", privateMsg);
            } else {
                System.err.println("USER_OFFLINE " + "User " + recipient + " is not online");
            }
        } else {
            JSONObject publicMsg = JsonBuilder.create()
                .put("sender", username)
                .put("message", messageText)
                .put("isPrivate", false)
                .build();

            for (ClientConnectionController client : server.getConnectedClients().values()) {
                client.sendMessage("CHAT", publicMsg);
            }
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

    private void newLobby(JSONObject data) {
        String name = JsonParser.getString(data, "name", "");
        int maxPlayers = JsonParser.getInt(data, "maxPlayers", 4);
        boolean isPrivate = JsonParser.getBoolean(data, "isPrivate", false);
        boolean isVisible = JsonParser.getBoolean(data, "isVisible", true);
        String password = JsonParser.getString(data, "password", "");

        if (isPrivate && password.isEmpty()) {
            System.err.println("Lobby does not created " + "Private Lobby should have a password");
            return;
        }
        try {
            server.createLobby(name, username, password, maxPlayers, isPrivate, isVisible);
        } catch (Exception e) {
            sendError("LOBBY_DOESNT_CREATED", "Error creating lobby: " + e.getMessage());
        }
    }

    private void processJoinLobby(JSONObject data) {
        String lobbyId = JsonParser.getString(data, "lobbyId", "");
        String password = JsonParser.getString(data, "password", "");
        try {
            server.joinLobby(lobbyId, username, password);
        } catch (Exception e) {
            sendError("Error", "Error joining lobby: " + e.getMessage());
        }
    }

    private void receiveStateToStart(JSONObject data) {
        String farm = JsonParser.getString(data, "farmName", "Standard");
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        if (game == null) {
            sendError("GAME_NOT_FOUND", "Game not found for your lobby");
            return;
        }
        farmName selected = farmName.STANDARD;
        for (farmName fa : farmName.values()) {
            if (fa.getFarmName().equalsIgnoreCase(farm)) {
                selected = fa;
                break;
            }
        }
        game.getPlayerState(username).setFarmName(selected);
    }

    private void readyToStart(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        if (game == null) {
            sendError("GAME_NOT_FOUND", "Game not found for your lobby");
            return;
        }
        game.getPlayerState(username).setReadyToPlay(true);
        if (game.allAreReadyToPlay()) {
            currentLobby.broadcastMessage("START", "all are ready to play");
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
                server.broadcast("Lobby: " + lobby.getName() + " was removed");
            }
        } else {
            sendError("NOT_IN_LOBBY", "You are not in any lobby");
        }
    }


    private void startGame(JSONObject data) {
        String lobbyId = JsonParser.getString(data, "lobbyId", "");
        if (lobbyId.isEmpty()) {
            sendError("error", "Lobby ID is required");
            return;
        }
        try {
            server.startGame(lobbyId, username);
        } catch (Exception e) {
            sendError("GAME_START_FAILED", "Error starting game: " + e.getMessage());
        }
    }

    private void playing(JSONObject data) {
        sendMessage("START_PLAYING", data);
    }


    private void sendPlayersInGame() {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        if (game == null) {
            sendError("GAME_NOT_FOUND", "Game not found for your lobby");
            return;
        }
    }

    private void sendPlayerPositions(JSONObject data) {
        sendMessage("PLAYER_POSITIONS", data);
    }

    private void handlePlayerMovement(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        if (game == null) {
            sendError("GAME_NOT_FOUND", "Game not found for your lobby");
            return;
        }
        PlayerInGame player = game.getPlayerState(username);
        if (player == null) {
            sendError("PLAYER_NOT_FOUND", "Player not found for your lobby");
            return;
        }
        float x = data.getFloat("x");
        float y = data.getFloat("y");
        String mapName = data.getString("mapName");
        player.setPosition(new Position(x, y), mapName);
        currentLobby.sendPlayerPositions();
        System.out.println("ClientController  sending position " + x + " " + y);
    }

    private void updateTimeInGame(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        float delta = data.getFloat("delta");
        game.updateTime(delta);
    }

    public synchronized void sendMessage(String type, JSONObject data) {
        if (!running.get() || out == null) {
            return;
        }
        try {
            Command message = new Command(type, data != null ? data : JsonBuilder.empty());
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

    private void syncGameTime(JSONObject data) {
        sendMessage("GAME_TIME", data);
    }
    private void changeGameWeather(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        String weather = JsonParser.getString(data, "weather", "");
        Weather weather1 = Weather.SUNNY;
        switch (weather) {
            case "sunny":
                weather1 = Weather.SUNNY;
                break;
                case "rainy":
                    weather1 = Weather.RAINY;
                    break;
                    case "stormy":
                        weather1 = Weather.STORMY;
                        break;
                        case "snowy":
                            weather1 = Weather.SNOWY;
                            break;
        }
        game.changeWeather(weather1);
    }
    private void changeHour(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        int hour = JsonParser.getInt(data, "hour", 0);
        game.changeTime(hour);
    }

    private void changeDay(JSONObject data) {
        Game game = server.getGameManager().getGameInstance(currentLobby.getId());
        int day = JsonParser.getInt(data, "day", 0);
        game.changeDay(day);
    }

    public void sendLobbiesList() {
        System.out.println("ClientConnectionController Sending lobbies list");
        JSONArray lobbiesArray = new JSONArray();

        for (GameLobby lobby : server.getGameLobbies().values()) {
            System.out.println("sending  " + lobby.getName() + " lobby: " + lobby.getId());
            JSONObject lobbyInfo = lobby.getLobbyInfo();

            if (lobby.hasPlayer(username)) {
                JSONArray players = new JSONArray();
                for (String playerName : lobby.getPlayers().keySet()) {
                    players.put(playerName);
                }
                lobbyInfo.put("players", players);
            }
            lobbiesArray.put(lobbyInfo);
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
