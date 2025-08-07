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
import com.proj.Control.MainMenuController;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.View.MainMenuView;
import com.proj.network.client.GameClient;
import com.proj.network.client.LobbyEventListener;
import com.proj.network.client.LobbyListListener;
import com.proj.network.client.NetworkEventListener;
import com.proj.network.event.LobbyEvent;
import com.proj.network.event.NetworkEvent;
import org.json.JSONException;
import org.json.JSONObject;


import javax.swing.*;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyScreen implements Screen, LobbyEventListener, LobbyListListener {

    private  Main game;
    private  GameClient gameClient;
    private Stage stage;
    private Table mainTable;
    private Table lobbyListTable;
    private TextButton createButton;
    private TextButton refreshButton;
    private Label titleLabel;
    private Label currentLobbyInfoLabel;
    private Table currentLobbyInfoTable;
    private TextButton startButton;
    private TextButton leaveButton;
    private Skin skin;

    private String currentLobbyInfoId;
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

        titleLabel = new Label("Multiplayer LobbyInfo", skin, "default");
        mainTable.add(titleLabel).padBottom(30).colspan(3).row();

        currentLobbyInfoLabel = new Label("Your LobbyInfo:", skin);
        mainTable.add(currentLobbyInfoLabel).pad(10).colspan(3).align(Align.left).row();

        currentLobbyInfoTable = new Table(skin);
        currentLobbyInfoTable.setBackground("background");
        mainTable.add(currentLobbyInfoTable).colspan(3).fillX().padBottom(20).row();

        startButton = new TextButton("Start Game", skin);
        leaveButton = new TextButton("Leave LobbyInfo", skin);

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

        createButton = new TextButton("Create LobbyInfo", skin);
        refreshButton = new TextButton("Refresh List", skin);

        Table bottomTable = new Table();
        bottomTable.add(createButton).padRight(10);
        bottomTable.add(refreshButton).padRight(10);
        mainTable.add(bottomTable).colspan(3).pad(20).row();

        setupEventListeners();
        gameClient.addLobbyListener(this);

        refreshLobbyInfoList();
        updateCurrentLobbyInfo();
    }

    private void setupEventListeners() {
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateLobbyInfoDialog();
            }
        });

        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshLobbyInfoList();
            }
        });


        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyInfoId != null && isAdmin) {
                    gameClient.startGame(currentLobbyInfoId);
                }
            }
        });
        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyInfoId != null) {
                    gameClient.leaveLobby();
                    currentLobbyInfoId = null;
                    isAdmin = false;
                    updateCurrentLobbyInfo();
                }
            }
        });
    }

    private void showCreateLobbyInfoDialog() {
        Dialog dialog = new Dialog("Create New LobbyInfo", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String name = ((TextField)getContentTable().findActor("name")).getText();
                    boolean isPrivate = ((CheckBox)getContentTable().findActor("private")).isChecked();
                    String password = ((TextField)getContentTable().findActor("password")).getText();
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

        content.add(new Label("LobbyInfo Name:", skin)).padRight(10);
        TextField nameField = new TextField("", skin);
        nameField.setName("name");
        content.add(nameField).width(200).row();

        content.add(new Label("LobbyInfo Type:", skin)).padRight(10);
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

    private void refreshLobbyInfoList() {
        gameClient.requestLobbiesList();
    }

    private void joinLobbyInfo(LobbyInfo lobby) {
        if (lobby.isPrivate()) {
            showPasswordDialog(lobby);
        } else {
            attemptJoinLobbyInfo(lobby.getId(), "");
        }
    }

    private void showPasswordDialog(LobbyInfo lobby) {
        Dialog dialog = new Dialog("Join Private LobbyInfo", skin);

        Table content = dialog.getContentTable();
        content.pad(15);

        content.add(new Label("Enter password for:", skin)).colspan(2).row();
        content.add(new Label(lobby.getName(), skin, "bold")).colspan(2).padBottom(10).row();

        content.add(new Label("Password:", skin)).padRight(10);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        content.add(passwordField).width(200).row();

        TextButton joinBtn = new TextButton("Join", skin);
        TextButton cancelBtn = new TextButton("Cancel", skin);

        // Add buttons to dialog
        dialog.getButtonTable().add(joinBtn).padRight(10);
        dialog.getButtonTable().add(cancelBtn);

        // Set result converter
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attemptJoinLobbyInfo(lobby.getId(), passwordField.getText());
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

    private void attemptJoinLobbyInfo(String lobbyId, String password) {
        gameClient.joinLobby(lobbyId, password);
    }

    private void updateCurrentLobbyInfo() {
        currentLobbyInfoTable.clear();
        if (currentLobbyInfoId == null) {
            currentLobbyInfoTable.add("Not in any lobby").pad(20);
            startButton.setVisible(false);
            leaveButton.setVisible(false);
            return;
        }

        LobbyInfo lobby = lobbiesMap.get(currentLobbyInfoId);
        if (lobby == null) {
            currentLobbyInfoTable.add("LobbyInfo information not available").pad(20);
            return;
        }

        currentLobbyInfoTable.add(new Label("LobbyInfo:", skin)).padRight(5);
        currentLobbyInfoTable.add(lobby.getName()).left().width(150).padRight(20);

        currentLobbyInfoTable.add(new Label("Players:", skin)).padRight(5);
        currentLobbyInfoTable.add(lobby.getPlayerCount() + "/" + lobby.getMaxPlayers()).left().width(50).padRight(20);

        currentLobbyInfoTable.add(new Label("Status:", skin)).padRight(5);
        currentLobbyInfoTable.add(lobby.isPrivate() ? "Private" : "Public").left().row();

        currentLobbyInfoTable.add((CharSequence) new JPopupMenu.Separator()).colspan(6).fillX().row();

        for (String player : lobby.getPlayers()) {
            Label playerLabel = new Label(player, skin);
            if (player.equals(lobby.getOwner())) {
                playerLabel.setText(player + " (Admin)");
                playerLabel.setColor(1, 0.8f, 0, 1);
            }
            currentLobbyInfoTable.add(playerLabel).colspan(6).align(Align.left).pad(3).row();
        }

        startButton.setVisible(isAdmin);
        leaveButton.setVisible(true);
    }

    @Override
    public void handleLobbyEvent(LobbyEvent event) {
        Gdx.app.postRunnable(() -> {
            switch (event.getType()) {
                case LOBBY_CREATED:
                    handleLobbyInfoCreated(event.getLobbyData());
                    break;

                case JOIN_SUCCESS:
                    handleJoinSuccess(event.getLobbyData());
                    break;

                case LEFT:
                    refreshLobbyInfoList();
                    refreshLobbyInfoListUI();
                    break;

                case GAME_STARTED:
                    game.switchToGameScreen();
                    break;

                case ERROR:
                    showError(event.getLobbyData());
                    break;
            }
        });
    }

    private void updateLobbiesList(String jsonData) {
        try {
            JSONObject jsonLobbies = new JSONObject(jsonData);
            lobbiesMap.clear();

            for (String lobbyId : jsonLobbies.keySet()) {
                JSONObject lobbyJson = jsonLobbies.getJSONObject(lobbyId);
                lobbiesMap.put(lobbyId, parseLobbyInfo(lobbyId, lobbyJson));
            }
            refreshLobbyInfoListUI();
        } catch (JSONException e) {
            showError("Error processing lobbies list");
        }
    }

    private LobbyInfo parseLobbyInfo(String lobbyId, JSONObject json) {
        LobbyInfo lobby = new LobbyInfo();
        lobby.setId(lobbyId);
        lobby.setName(json.getString("name"));
        lobby.setOwner(json.getString("owner"));
        lobby.setPlayerCount(json.getInt("playerCount"));
        lobby.setMaxPlayers(json.getInt("maxPlayers"));
        lobby.setPrivate(json.getBoolean("isPrivate"));
        lobby.setGameActive(json.getBoolean("isGameActive"));

        java.util.List<String> players = new ArrayList<>();
        JSONObject playersJson = json.getJSONObject("players");
        for (String player : playersJson.keySet()) {
            players.add(player);
        }
        lobby.setPlayers(players);
        return lobby;
    }

    private void refreshLobbyInfoListUI() {
        lobbyListTable.clear();

        if (lobbiesMap.isEmpty()) {
            lobbyListTable.add("No public lobbies available").pad(20);
            return;
        }
        lobbyListTable.add("LobbyInfo Name").pad(10).width(150);
        lobbyListTable.add("Players").pad(10).width(80);
        lobbyListTable.add("Status").pad(10).width(100);
        lobbyListTable.add("").pad(10).width(100).row();
        lobbyListTable.add((CharSequence) new JPopupMenu.Separator()).colspan(4).fillX().row();

        for (LobbyInfo lobby : lobbiesMap.values()) {
            if (lobby.getId().equals(currentLobbyInfoId)) continue;

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
                    joinLobbyInfo(lobby);
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

    private void handleLobbyInfoCreated(String jsonData) {
        try {
            JSONObject lobbyJson = new JSONObject(jsonData);
            String lobbyId = lobbyJson.getString("id");

            LobbyInfo lobby = parseLobbyInfo(lobbyId, lobbyJson);
            lobbiesMap.put(lobbyId, lobby);

            currentLobbyInfoId = lobbyId;
            isAdmin = true;
            updateCurrentLobbyInfo();
            refreshLobbyInfoListUI();
        } catch (JSONException e) {
            showError("Error processing created lobby");
        }
    }

    private void handleJoinSuccess(String jsonData) {
        try {
            JSONObject lobbyJson = new JSONObject(jsonData);
            String lobbyId = lobbyJson.getString("id");

            LobbyInfo lobby = parseLobbyInfo(lobbyId, lobbyJson);
            lobbiesMap.put(lobbyId, lobby);

            currentLobbyInfoId = lobbyId;
            isAdmin = lobby.getOwner().equals(gameClient.getUsername());
            updateCurrentLobbyInfo();
        } catch (JSONException e) {
            showError("Error processing joined lobby");
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

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void handleNetworkEvent(NetworkEvent event) {

    }

    @Override
    public void onLobbiesReceived(JSONObject lobbiesData) {
        updateLobbiesList(lobbiesData.toString());
    }

    public class LobbyInfo {
        private String id;
        private String name;
        private String owner;
        private int playerCount;
        private int maxPlayers;
        private boolean isPrivate;
        private boolean isGameActive;
        private java.util.List<String> players = new ArrayList<>();

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        public int getPlayerCount() { return playerCount; }
        public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }

        public int getMaxPlayers() { return maxPlayers; }
        public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

        public boolean isPrivate() { return isPrivate; }
        public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

        public boolean isGameActive() { return isGameActive; }
        public void setGameActive(boolean isGameActive) { this.isGameActive = isGameActive; }

        public java.util.List<String> getPlayers() { return players; }
        public void setPlayers(List<String> players) { this.players = players; }
    }
}
