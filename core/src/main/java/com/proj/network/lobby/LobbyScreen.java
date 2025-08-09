package com.proj.network.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyScreen implements Screen, LobbyEventListener, LobbyListListener {

    private Main game;
    private GameClient gameClient;
    private Stage stage;
    private Table mainTable;
    private Table lobbyListTable;
    private TextButton createButton;
    private Label titleLabel;
    private Label currentLobbyInfoLabel;
    private Table currentLobbyTable;
    private TextButton startButton;
    private TextButton leaveButton;
    private Skin skin;

    private String currentLobbyId;
    private boolean isAdmin = false;
    private Map<String, LobbyInfo> lobbiesMap = new HashMap<>();
    private Texture backgroundTexture;
    private Image backgroundImage;

    private TextField searchField;
    private TextButton searchButton;
    private TextButton clearSearchButton;
    private boolean isSearchByID = false;
    private String currentSearchId = "";

    public LobbyScreen(Main game) {
        this.game = game;
        this.gameClient = game.getGameClient();
        this.skin = GameAssetManager.getGameAssetManager().getStardewSkin();
        backgroundTexture = GameAssetManager.getGameAssetManager().getLobbyBackgroundTexture(0);
        stage = new Stage(new ScreenViewport());
        gameClient.addLobbyEventListener(this);
        gameClient.addLobbyListListener(this);
        System.err.println("request constructor");
        refreshLobbyList();
    }

    private void setupEventListeners() {
        searchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String searchId = searchField.getText().trim();
                if (!searchId.isEmpty()) {
                    isSearchByID = true;
                    currentSearchId = searchId;
                    refreshLobbyListUI();
                }
            }
        });

        clearSearchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isSearchByID = false;
                currentSearchId = "";
                searchField.setText("");
                refreshLobbyListUI();
            }
        });

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreateLobbyDialog();
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
                    boolean isVisible = ((CheckBox) getContentTable().findActor("visible")).isChecked(); // چک‌باکس جدید
                    String password = ((TextField) getContentTable().findActor("password")).getText();

                    int maxPlayers = (int) ((Slider) getContentTable().findActor("maxPlayersSlider")).getValue();

                    if (isPrivate && password.isEmpty()) {
                        showError("Password is required for private lobbies");
                        return;
                    }
                    gameClient.createLobby(name, password, maxPlayers, isPrivate, isVisible); // استفاده از isVisible
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
        Table typeTable = new Table();
        CheckBox privateCheckbox = new CheckBox(" Private", skin);
        privateCheckbox.setName("private");
        typeTable.add(privateCheckbox).left();

        CheckBox visibleCheckbox = new CheckBox(" Visible", skin);
        visibleCheckbox.setName("visible");
        visibleCheckbox.setChecked(true); // پیش‌فرض فعال
        typeTable.add(visibleCheckbox).padLeft(20);
        content.add(typeTable).left().row();

        content.add(new Label("Password:", skin)).padRight(10);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setName("password");
        passwordField.setDisabled(true);
        content.add(passwordField).width(200).row();

        content.add(new Label("Max Players:", skin)).padRight(10);
        Table sliderTable = new Table();
        Slider maxPlayersSlider = new Slider(2, 4, 1, false, skin);
        maxPlayersSlider.setValue(4);
        maxPlayersSlider.setName("maxPlayersSlider");
        sliderTable.add(maxPlayersSlider).width(150);

        Label maxPlayersValue = new Label("4", skin);
        sliderTable.add(maxPlayersValue).padLeft(10);

        maxPlayersSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int) maxPlayersSlider.getValue();
                maxPlayersValue.setText(String.valueOf(value));
            }
        });

        content.add(sliderTable).row();

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
        System.err.println("requst lobby list LobbyScreen");
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
        content.add(new Label(lobby.getName(), skin )).colspan(2).padBottom(10).row();

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
        currentLobbyTable.add(lobby.getName()).left().width(100).padRight(10);

        currentLobbyTable.add(new Label("Players:", skin)).padRight(10);
        currentLobbyTable.add(lobby.getPlayerCount() + "/" + lobby.getMaxPlayers()).left().width(50).padRight(20);

        currentLobbyTable.add(new Label("Status:", skin)).padRight(10);
        currentLobbyTable.add(lobby.isPrivate() ? "Private" : "Public").left().row();

        for (String player : lobby.getPlayers()) {
            Label playerLabel = new Label(player, skin);
            if (player.equals(lobby.getOwner())) {
                playerLabel.setText(player + " (Admin)");
                playerLabel.setColor(1, 0, 1, 1);
            }
            currentLobbyTable.add(playerLabel).colspan(6).align(Align.left).pad(3).row();
        }

        startButton.setVisible(isAdmin);
        leaveButton.setVisible(true);
    }

    @Override
    public void handleLobbyEvent(LobbyEvent event) {
        Gdx.app.postRunnable(() -> {
            switch (event.getType()) {
                case LOBBY_CREATED:
                    handleLobbyCreated(event.getLobbyData());
                    break;

                case LOBBY_ADDED:
                    refreshLobbyList();
                    refreshLobbyListUI();
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

                case LOBBY_UPDATE:
                    System.out.println("lobby info updated");
                    handleLobbyUpdate(event.getLobbyData());
                break;

                case ERROR:
                    showError(event.getLobbyData());
                    break;
            }
        });
    }

    private void handleLeaveLobby() {
        currentLobbyId = null;
        isAdmin = false;
        System.err.println("handle leave lobby requested");
        refreshLobbyList();
        updateCurrentLobbyInfo();
        refreshLobbyListUI();
    }
    private void handleLobbyUpdate(String data) {
        refreshLobbyList();
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
                System.out.println("received" + lobby.getName());

                lobbiesMap.put(lobby.getId(), lobby);
            }

            updateCurrentLobbyInfo();
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
        lobby.setVisible(json.getBoolean("isVisible"));

        List<String> players = new ArrayList<>();
        if (json.has("players")) {
            JSONArray playersJson = json.getJSONArray("players");
            for (Object player : playersJson) {
                players.add((String) player);
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
        lobbyListTable.row();
        lobbyListTable.add("Name").width(100).left();
        lobbyListTable.add("Players").pad(15).width(180);
        lobbyListTable.add("Status").pad(20).width(150);
        lobbyListTable.row();

        if (isSearchByID) {
            LobbyInfo foundLobby = null;

            // جستجو در لابی‌ها
            for (LobbyInfo lobby : lobbiesMap.values()) {
                if (lobby.getId().equals(currentSearchId)) {
                    foundLobby = lobby;
                    break;
                }
            }

            if (foundLobby != null) {
                addLobbyToTable(foundLobby);
            } else {
                lobbyListTable.add("No lobby found with ID: " + currentSearchId).colspan(4).pad(20);
            }
            return;
        }

        // Lobby rows
        for (LobbyInfo lobby : lobbiesMap.values()) {
            if (lobby.isVisible() || lobby.players.contains(gameClient.getUsername())) {
                // Skip current lobby
//            if (lobby.getId().equals(currentLobbyId)) continue;
                addLobbyToTable(lobby);
            }
        }
    }

    private void addLobbyToTable(LobbyInfo lobby) {
        lobbyListTable.add(lobby.getName()).left();
        lobbyListTable.add(lobby.getPlayerCount() + "/" + lobby.getMaxPlayers()).pad(5);

        String status = lobby.isPrivate() ? "Private" : "Public";
        if (lobby.isGameActive()) {
            status += " (Playing)";
        }
        lobbyListTable.add(status);
        if (!lobby.getId().equals(currentLobbyId)) {
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
            lobbyListTable.add(joinButton).pad(5).width(100);
        }
        lobbyListTable.row();
    }

    private void handleLobbyCreated(String jsonData) {
        try {
            System.err.println("LobbyScreen      jadiddddddd");
            JSONObject lobbyJson = new JSONObject(jsonData);
            LobbyInfo lobby = parseLobbyInfo(lobbyJson);
            lobbiesMap.put(lobby.getId(), lobby);

            currentLobbyId = lobby.getId();
            isAdmin = true;
            updateCurrentLobbyInfo();
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

    private float duration = 0;
    private int num = 0;

    @Override
    public void render(float delta) {
        duration += delta;
        ScreenUtils.clear(1, 0.7f, 0.7f, 1);
        if (duration >= 5f) {
            if (num == 5) {
                num = 0;
            } else {
                num++;
            }
            Texture newTex = GameAssetManager
                .getGameAssetManager()
                .getLobbyBackgroundTexture(num);
            backgroundImage.setDrawable(
                new TextureRegionDrawable(new TextureRegion(newTex))
            );
            duration = 0f;
        }
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private static boolean hasShownOnce = false;

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        titleLabel = new Label("Multiplayer Lobby", skin);
        mainTable.add(titleLabel).padBottom(30).colspan(3).row();

        currentLobbyInfoLabel = new Label("Your Lobby:", skin);
        mainTable.add(currentLobbyInfoLabel).pad(10).colspan(3).align(Align.center).row();

        currentLobbyTable = new Table(skin);
        currentLobbyTable.setBackground("background");
        currentLobbyTable.setColor(1f, 1f, 1f, 0.8f);
        mainTable.add(currentLobbyTable).colspan(3).
            minWidth(400).minHeight(150).expandY().fillY().padBottom(10).row();

        startButton = new TextButton("Start Game", skin);
        leaveButton = new TextButton("Leave Lobby", skin);

        Table buttonTable = new Table();
        buttonTable.add(startButton).padRight(10);
        buttonTable.add(leaveButton);
        mainTable.add(buttonTable).colspan(3).padBottom(30).row();

        mainTable.add(new Label("Available Lobbies:", skin)).pad(10).colspan(3).align(Align.center).row();
        mainTable.add(new Label("Search by ID:", skin));
        Table searchTable = new Table();
        searchField = new TextField("", skin);
        searchField.setMessageText("Enter Lobby ID");
        searchTable.add(searchField).width(400).padRight(5);

        searchButton = new TextButton("Search", skin);
        searchTable.add(searchButton).padRight(5);

        clearSearchButton = new TextButton("Clear", skin);
        searchTable.add(clearSearchButton);

        mainTable.add(searchTable).colspan(2).align(Align.left).row();

        lobbyListTable = new Table(skin);
        lobbyListTable.setBackground("background");
        ScrollPane scrollPane = new ScrollPane(lobbyListTable, skin);
        scrollPane.setFadeScrollBars(true);
        lobbyListTable.setColor(1f, 1f, 1f, 0.8f);
        mainTable.add(scrollPane)
            .colspan(3)
            .minHeight(250)
            .maxWidth(800)
            .expandY()
            .fillY()
            .pad(10)
            .row();

        createButton = new TextButton("Create Lobby", skin);

        Table bottomTable = new Table();
        bottomTable.add(createButton).center();
        mainTable.add(bottomTable).colspan(3).pad(20).row();

        setupEventListeners();

//        if (!hasShownOnce) {
//            System.err.println("show request");
        updateCurrentLobbyInfo();
//            hasShownOnce = true;
//        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        gameClient.removeLobbyEventListener(this);
        gameClient.removeLobbyListListener(this);
    }

    @Override
    public void dispose() {
        gameClient.removeLobbyEventListener(this);
        gameClient.removeLobbyListListener(this);
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
        private boolean isVisible;
        private List<String> players = new ArrayList<>();

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public boolean isVisible() {
            return isVisible;
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
