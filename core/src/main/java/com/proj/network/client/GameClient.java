{
    private static final long PING_INTERVAL = 15000; // 15 seconds

    private final String serverAddress;
    private final int serverPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread clientToServerThread;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final List<NetworkEventListener> listeners = new ArrayList<>();
    List<LobbyEventListener> lobbyListeners = new ArrayList<>();
    List<LobbyListListener> lobbyListListeners = new ArrayList<>();
    List<GameEventListener> gameListeners = new ArrayList<>();

    private String username;
    private String currentLobbyId;
    private boolean authenticated = false;

    private final List<ChatListener> chatListeners = new ArrayList<>();

    public GameClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() {
        if (isRunning.get()) return;

        try {
            clientSocket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            isRunning.set(true);
            if (clientToServerThread == null || !clientToServerThread.isAlive()) {
                clientToServerThread = new Thread(this, "Network-Thread");
                clientToServerThread.start();
            }

            startPingTask();
            sendEvent(NetworkEvent.Type.CONNECTED, "client is connected to server");
        } catch (IOException e) {
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
    public void addGameListener(GameEventListener listener) {
        if (!gameListeners.contains(listener)) {
            gameListeners.add(listener);
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
    public void removeGameListener(GameEventListener listener) {
        gameListeners.remove(listener);
    }


    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                String command = in.readLine();
                if (command == null) break;
                handleCommands(command);
            }

        } catch (IOException e) {
            if (isRunning.get()) {
                System.err.println("Connection lost");
            }
        } finally {
            disconnect();
        }
    }

    private void handleCommands(String command) {
        try {
            // Use the NetworkMessage factory for safe parsing
            Command message = Command.parse(command);
            processCommand(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCommand(Command message) {
        try {
            String type = message.getType();
            JSONObject data = message.getData();

            switch (type) {
                case "LOGIN_SUCCESSFUL":
                    handleAuthSuccess(data);
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

                case "GAME_TIME":
                    syncGameTime(data);
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
                    sendEvent(NetworkEvent.Type.SYSTEM_MESSAGE,
                        JsonParser.getString(data, "message", "System message"));
                    break;

                case "START":
                    fireGameEvent(GameEvent.Type.START, JsonBuilder.empty());
                    break;

                    case "PLAYER_POSITIONS":
                        fireGameEvent(GameEvent.Type.UPDATE_POSITIONS, data );
                        break;

                case "PONG":
                    // Ping response - no action needed
                    break;

                case "ERROR":
                    handleError(data);
                    break;
                case "CHAT":
                    String sender = JsonParser.getString(data, "sender", "Unknown");
                    String chatMessage = JsonParser.getString(data, "message", "");
                    boolean isPrivate = JsonParser.getBoolean(data, "isPrivate", false);

                    // Send to any chat listeners
                    for (NetworkEventListener listener : listeners) {
                        if (listener instanceof ChatListener) {
                            ((ChatListener) listener).onChatMessage(sender, chatMessage, isPrivate);
                        }
                    }
                    break;

                default:
                    sendEvent(NetworkEvent.Type.UNKNOWN_MESSAGE, message.toString());
                    break;
            }
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleAuthSuccess(JSONObject data) {
        try {
            this.username = JsonParser.getString(data, "username", "");
            if (!username.isEmpty()) {
                this.authenticated = true;
                sendEvent(NetworkEvent.Type.AUTH_SUCCESS, "Welcome " + username);
            } else {
                sendEvent(NetworkEvent.Type.ERROR, "login failed");
            }
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Invalid login data: " + e.getMessage());
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
            sendEvent(NetworkEvent.Type.ERROR, "Error processing lobby creation: " + e.getMessage());
        }
    }


    public void requestOnlinePlayers() {
        sendMessage("GET_ONLINE_PLAYERS", new JSONObject());
    }

    public void handleOnlinePlayersResponse(JSONObject data) {
        for (LobbyEventListener listener : lobbyListeners) {
            listener.handleLobbyEvent(
                new LobbyEvent(LobbyEvent.Type.ONLINE_PLAYERS_RECEIVED,
                    data.toString()));
        }
        for (ChatListener listener1 : chatListeners) {
            listener1.onReceivedPlayerList(data.toString());
            System.out.println("send for: " + listener1.toString());
        }
    }

    private void handleLobbyAdded(JSONObject data) {
        try {
            fireLobbyEvent(LobbyEvent.Type.LOBBY_ADDED, data);
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error processing lobby added: " + e.getMessage());
        }
    }

    private void handleJoinLobby(JSONObject data) {
        try {
            currentLobbyId = JsonParser.getString(data, "id", "");
            fireLobbyEvent(LobbyEvent.Type.JOIN_SUCCESS, data);
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error joining lobby: " + e.getMessage());
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
            for (ChatListener listener : chatListeners) {
                listener.onChatMessage(sender, message, true);
            }
//            fireChatEvent(NetworkEvent.Type.PRIVATE_MESSAGE, sender + ": " + message);
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error processing private chat: " + e.getMessage());
        }
    }


    private void handleError(JSONObject data) {
        try {
            String code = JsonParser.getString(data, "code", "UNKNOWN_ERROR");
            String message = JsonParser.getString(data, "message", "An error occurred");
            sendEvent(NetworkEvent.Type.ERROR, "[" + code + "] " + message);
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error processing error message: " + e.getMessage());
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

    public void sendStateToStartGame(farmName farm) {
        JSONObject data = JsonBuilder.create().
            put("farmName", farm.getFarmName()).build();
        sendMessage("STATE_TO_START_GAME", data);
    }

    public void readyToPlay() {
        JSONObject data = JsonBuilder.create().put("ready", true).build();
        sendMessage("READY_TO_PLAY", data);
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

    public void sendPlayerNewPosition(float x, float y, String mapName) {
        String map = mapName ;
        for (farmName f : farmName.values()) {
            if (mapName.equalsIgnoreCase(f.getFarmName())) {
                map = "Farm";
            }
        }
        JSONObject data = JsonBuilder.create().put("x", x).put("y", y).put("mapName", map).build();
        sendMessage("MOVE", data);
//        System.out.println("GameClient  sending position " + x + " " + y);
    }

    public void updateTimeInGame(float delta) {
        sendMessage("UPDATE_TIME_IN_GAME", JsonBuilder.create().put("delta", delta).build());
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

    public void syncGameTime(JSONObject data) {
//        System.out.println("GameClient  syncGameTime");
            fireGameEvent(GameEvent.Type.UPDATE_TIME, data);
    }

    private void sendMessage(String type, JSONObject data) {
        if (!isRunning.get() || out == null) return;

        try {
            Command message = new Command(type, data != null ? data : JsonBuilder.empty());
            out.println(message.toJsonString());
            out.flush();
        } catch (Exception e) {
            sendEvent(NetworkEvent.Type.ERROR, "Error sending message: " + e.getMessage());
        }
    }

    private void startPingTask() {
        new Thread(() -> {
            while (isRunning.get()) {
                try {
                    Thread.sleep(PING_INTERVAL);
                    sendMessage("PING", JsonBuilder.empty());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Ping-Thread").start();
    }


    public void changeWeather(String weatherType) {
        sendMessage("CHANGE_WEATHER", (JsonBuilder.create().put("weather", weatherType).build()));
    }

    public void changeHour(int hour) {
        sendMessage("CHANGE_HOUR", (JsonBuilder.create().put("hour", hour).build()));
    }

    public void changeDay(int day) {
        sendMessage("CHANGE_DAY", (JsonBuilder.create().put("day", day).build()));
    }


    public void disconnect() {
        if (isRunning.compareAndSet(true, false)) {
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
            sendEvent(NetworkEvent.Type.DISCONNECTED, "Disconnected from server");
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

    private void sendEvent(NetworkEvent.Type type, String message) {
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
        for (GameEventListener listener : gameListeners) {
                (listener).handleGameEvent(event);
        }
    }

    private void fireLobbyListEvent(JSONObject data) {
        for (LobbyListListener listener : lobbyListListeners) {
            (listener).onLobbiesReceived(data);
        }
    }

    public boolean isConnected() {
        return isRunning.get();
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

    public void addChatListener(ChatListener listener) {
        if (!chatListeners.contains(listener)) {
            chatListeners.add(listener);
        }
    }

    public void removeChatListener(ChatListener listener) {
        chatListeners.remove(listener);
    }
}
