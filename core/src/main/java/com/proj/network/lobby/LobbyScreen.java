package com.proj.network.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.network.client.GameClient;
import com.proj.network.client.LobbyEventListener;
import com.proj.network.client.LobbyListListener;
import com.proj.network.event.LobbyEvent;
import com.proj.network.event.NetworkEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.List;

public class LobbyScreen implements Screen, LobbyEventListener, LobbyListListener {

    private Main game;
    private GameClient gameClient;
    private Stage stage;
    private Table mainTable;
    private Table lobbyListTable;
    private TextButton createButton;
    private TextButton refreshButton;
    private Label titleLabel;
    private Label currentLobbyInfoLabel;
    private Table currentLobbyTable;
    private TextButton startButton;
    private TextButton leaveButton;
    private Skin skin;

    private String currentLobbyId;
    private boolean isAdmin = false;
    private Map<String, LobbyInfo> lobbiesMap = new HashMap<>();



    public LobbyScreen(Main game) {
        this.game = game;
        this.gameClient = game.getGameClient();
        this.skin = GameAssetManager.getGameAssetManager().getStardewSkin();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        titleLabel = new Label("Multiplayer Lobby", skin, "default");
        mainTable.add(titleLabel).padBottom(30).colspan(3).row();

        currentLobbyInfoLabel = new Label("Your Lobby:", skin);
        mainTable.add(currentLobbyInfoLabel).pad(10).colspan(3).align(Align.left).row();

        currentLobbyTable = new Table(skin);
        currentLobbyTable.setBackground("background");
        mainTable.add(currentLobbyTable).colspan(3).fillX().padBottom(20).row();

        startButton = new TextButton("Start Game", skin);
        leaveButton = new TextButton("Leave Lobby", skin);

        Table buttonTable = new Table();
        buttonTable.add(startButton).padRight(10);
        buttonTable.add(leaveButton);
        mainTable.add(buttonTable).colspan(3).padBottom(30).row();

        mainTable.add(new Label("Available Lobbies:", skin)).pad(10).colspan(3).align(Align.left).row();

        lobbyListTable = new Table(skin);
        lobbyListTable.setBackground("background");
        ScrollPane scrollPane = new ScrollPane(lobbyListTable, skin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).colspan(3).fill().expand().pad(10).row();

        createButton = new TextButton("Create Lobby", skin);
        refreshButton = new TextButton("Refresh List", skin);

        Table bottomTable = new Table();
        bottomTable.add(createButton).padRight(10);
        bottomTable.add(refreshButton).padRight(10);
        mainTable.add(bottomTable).colspan(3).pad(20).row();

        setupEventListeners();
        gameClient.addLobbyListener(this);
        gameClient.addLobbyListListener(this);

        refreshLobbyList();
        updateCurrentLobbyInfo();
    }

    private void setupEventListeners() {
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateLobbyDialog();
            }
        });

        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshLobbyList();
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyId != null && isAdmin) {
                    gameClient.startGame(currentLobbyId);
                }
            }
        });

        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyId != null) {
                    gameClient.leaveLobby();
                    currentLobbyId = null;
                    isAdmin = false;
                    updateCurrentLobbyInfo();
                }
            }
        });
    }

    private void showCreateLobbyDialog() {
        Dialog dialog = new Dialog("Create New Lobby", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String name = ((TextField) getContentTable().findActor("name")).getText();
                    boolean isPrivate = ((CheckBox) getContentTable().findActor("private")).isChecked();
                    String password = ((TextField) getContentTable().findActor("password")).getText();
                    int maxPlayers = 4;

                    if (isPrivate && password.isEmpty()) {
                        showError("Password is required for private lobbies");
                        return;
                    }
                    gameClient.createLobby(name, password, maxPlayers, isPrivate, true);
                }
            }
        };

        Table content = dialog.getContentTable();
        content.pad(15);

        content.add(new Label("Lobby Name:", skin)).padRight(10);
        TextField nameField = new TextField("", skin);
        nameField.setName("name");
        content.add(nameField).width(200).row();

        content.add(new Label("Lobby Type:", skin)).padRight(10);
        CheckBox privateCheckbox = new CheckBox(" Private", skin);
        privateCheckbox.setName("private");
        content.add(privateCheckbox).align(Align.left).row();

        content.add(new Label("Password:", skin)).padRight(10);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setName("password");
        passwordField.setDisabled(true);
        content.add(passwordField).width(200).row();

        privateCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                passwordField.setDisabled(!privateCheckbox.isChecked());
            }
        });

        dialog.button("Create", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }

    private void refreshLobbyList() {
        gameClient.requestLobbiesList();
    }

    private void joinLobby(LobbyInfo lobby) {
        if (lobby.isPrivate()) {
            showPasswordDialog(lobby);
        } else {
            attemptJoinLobby(lobby.getId(), "");
        }
    }

    private void showPasswordDialog(LobbyInfo lobby) {
        Dialog dialog = new Dialog("Join Private Lobby", skin);

        Table content = dialog.getContentTable();
        content.pad(15);

        content.add(new Label("Enter password for:", skin)).colspan(2).row();
        content.add(new Label(lobby.getName(), skin, "bold")).colspan(2).padBottom(10).row();

        content.add(new Label("Password:", skin)).padRight(10);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        content.add(passwordField).width(200).row();

        Table buttonTable = dialog.getButtonTable();
        TextButton joinBtn = new TextButton("Join", skin);
        TextButton cancelBtn = new TextButton("Cancel", skin);

        buttonTable.add(joinBtn).padRight(10);
        buttonTable.add(cancelBtn);

        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attemptJoinLobby(lobby.getId(), passwordField.getText());
                dialog.hide();
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }

    private void attemptJoinLobby(String lobbyId, String password) {
        gameClient.joinLobby(lobbyId, password);
    }

    private void updateCurrentLobbyInfo() {
        currentLobbyTable.clear();
        if (currentLobbyId == null) {
            currentLobbyTable.add("Not in any lobby").pad(20);
            startButton.setVisible(false);
            leaveButton.setVisible(false);
            return;
        }

        LobbyInfo lobby = lobbiesMap.get(currentLobbyId);
        if (lobby == null) {
            currentLobbyTable.add("Lobby information not available").pad(20);
            return;
        }

        currentLobbyTable.add(new Label("Lobby:", skin)).padRight(10);
        currentLobbyTable.add(lobby.getName()).left().width(150).padRight(20);

        currentLobbyTable.add(new Label("Players:", skin)).padRight(10);
        currentLobbyTable.add(lobby.getPlayerCount() + "/" + lobby.getMaxPlayers()).left().width(50).padRight(20);

        currentLobbyTable.add(new Label("Status:", skin)).padRight(10);
        currentLobbyTable.add(lobby.isPrivate() ? "Private" : "Public").left().row();

        for (String player : lobby.getPlayers()) {
            Label playerLabel = new Label(player, skin);
            if (player.equals(lobby.getOwner())) {
                playerLabel.setText(player + " (Admin)");
                playerLabel.setColor(1, 0.8f, 0, 1);
            }
            currentLobbyTable.add(playerLabel).colspan(6).align(Align.left).pad(3).row();
        }

        startButton.setVisible(isAdmin);
        leaveButton.setVisible(true);
    }

    @Override
    public void handleLobbyEvent(LobbyEvent event) {
        switch (event.getType()) {
            case LOBBY_CREATED:
                System.out.println("LobbyScreen : Lobby created");
                handleLobbyCreated(event.getLobbyData());
                break;

            case JOIN_SUCCESS:
                handleJoinSuccess(event.getLobbyData());
                break;

            case LEFT:
                handleLeaveLobby();
                break;

            case GAME_STARTED:
                game.switchToGameScreen();
                break;

            case ERROR:
                showError(event.getLobbyData());
                break;
        }

    }

    private void handleLeaveLobby() {
        currentLobbyId = null;
        isAdmin = false;
        refreshLobbyList();
        updateCurrentLobbyInfo();
        refreshLobbyListUI();
    }

    @Override
    public void onLobbiesReceived(JSONObject lobbiesData) {
        Gdx.app.postRunnable(() -> updateLobbiesList(lobbiesData));
    }

    private void updateLobbiesList(JSONObject lobbiesData) {
        try {
            lobbiesMap.clear();
            JSONArray lobbiesArray = lobbiesData.getJSONArray("lobbies");

            for (int i = 0; i < lobbiesArray.length(); i++) {
                JSONObject lobbyJson = lobbiesArray.getJSONObject(i);
                LobbyInfo lobby = parseLobbyInfo(lobbyJson);
                lobbiesMap.put(lobby.getId(), lobby);
            }
            refreshLobbyListUI();
        } catch (JSONException e) {
            showError("Error loading lobbies: " + e.getMessage());
        }
    }

    private LobbyInfo parseLobbyInfo(JSONObject json) throws JSONException {
        LobbyInfo lobby = new LobbyInfo();
        lobby.setId(json.getString("id"));
        lobby.setName(json.getString("name"));
        lobby.setOwner(json.getString("owner"));
        lobby.setPlayerCount(json.getInt("playerCount"));
        lobby.setMaxPlayers(json.getInt("maxPlayers"));
        lobby.setPrivate(json.getBoolean("isPrivate"));
        lobby.setGameActive(json.getBoolean("isGameActive"));

        List<String> players = new ArrayList<>();
        if (json.has("players")) {
            JSONObject playersJson = json.getJSONObject("players");
            for (String player : playersJson.keySet()) {
                players.add(player);
            }
        }
        lobby.setPlayers(players);
        return lobby;
    }

    private void refreshLobbyListUI() {
        lobbyListTable.clear();

        if (lobbiesMap.isEmpty()) {
            lobbyListTable.add("No public lobbies available").pad(20);
            return;
        }

        // Header row
        lobbyListTable.add("Name").pad(20).width(100);
        lobbyListTable.add("Players").pad(20).width(100);
        lobbyListTable.add("Status").pad(20).width(150);
        lobbyListTable.add("").pad(20).width(150).row();

        // Lobby rows
        for (LobbyInfo lobby : lobbiesMap.values()) {
            // Skip current lobby
            if (lobby.getId().equals(currentLobbyId)) continue;

            lobbyListTable.add(lobby.getName()).pad(5).left();
            lobbyListTable.add(lobby.getPlayerCount() + "/" + lobby.getMaxPlayers()).pad(5);

            String status = lobby.isPrivate() ? "Private" : "Public";
            if (lobby.isGameActive()) {
                status += " (Playing)";
            }
            lobbyListTable.add(status).pad(5);

            TextButton joinButton = new TextButton("Join", skin);
            joinButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    joinLobby(lobby);
                }
            });

            if (lobby.getPlayerCount() >= lobby.getMaxPlayers() || lobby.isGameActive()) {
                joinButton.setDisabled(true);
                joinButton.setText("Full");
            }

            lobbyListTable.add(joinButton).pad(5).width(80);
            lobbyListTable.row();
        }
    }

    private void handleLobbyCreated(String jsonData) {
        try {
            JSONObject lobbyJson = new JSONObject(jsonData);
            LobbyInfo lobby = parseLobbyInfo(lobbyJson);
            lobbiesMap.put(lobby.getId(), lobby);

            currentLobbyId = lobby.getId();
            isAdmin = true;

            refreshLobbyList();
            updateCurrentLobbyInfo();
            refreshLobbyListUI();
        } catch (JSONException e) {
            showError("Error creating lobby: " + e.getMessage());
        }
    }

    private void handleJoinSuccess(String jsonData) {
        try {
            JSONObject lobbyJson = new JSONObject(jsonData);
            LobbyInfo lobby = parseLobbyInfo(lobbyJson);
            lobbiesMap.put(lobby.getId(), lobby);

            currentLobbyId = lobby.getId();
            isAdmin = lobby.getOwner().equals(gameClient.getUsername());
            updateCurrentLobbyInfo();
            refreshLobbyListUI();
        } catch (JSONException e) {
            showError("Error joining lobby: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Dialog dialog = new Dialog("Error", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
//        gameClient.removeLobbyListener(this);
//        gameClient.removeLobbyListListener(this);
        stage.dispose();
    }

    @Override
    public void handleNetworkEvent(NetworkEvent event) {
        // Handle network events if needed
    }

    public class LobbyInfo {
        private String id;
        private String name;
        private String owner;
        private int playerCount;
        private int maxPlayers;
        private boolean isPrivate;
        private boolean isGameActive;
        private List<String> players = new ArrayList<>();

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public void setPlayerCount(int playerCount) {
            this.playerCount = playerCount;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        public void setPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public boolean isGameActive() {
            return isGameActive;
        }

        public void setGameActive(boolean isGameActive) {
            this.isGameActive = isGameActive;
        }

        public List<String> getPlayers() {
            return players;
        }

        public void setPlayers(List<String> players) {
            this.players = players;
        }
    }
}
