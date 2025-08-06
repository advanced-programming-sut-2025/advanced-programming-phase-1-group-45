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
import com.proj.Model.GameAssetManager;
import com.proj.network.GameClient;
import com.proj.network.GameLobby;

public class LobbyScreen implements Screen {

    private Stage stage;
    private Table mainTable;
    private Table lobbyListTable;
    private TextButton createButton;
    private TextButton refreshButton;
    private TextButton backButton;
    private Label titleLabel;
    private Label currentLobbyLabel;
    private Table currentLobbyTable;
    private TextButton startButton;
    private TextButton leaveButton;

    private String currentLobbyId;
    private boolean isAdmin = false;
    private Skin skin;

    public LobbyScreen(GameClient gameClient) {
        this.gameClient = gameClient;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        skin = GameAssetManager.getGameAssetManager().getStardewSkin();

        titleLabel = new Label("Multiplayer Lobby", skin, "default");
        mainTable.add(titleLabel).padBottom(30).colspan(3).row();

        currentLobbyLabel = new Label("Your Lobby:", skin);
        mainTable.add(currentLobbyLabel).pad(10).colspan(3).align(Align.left).row();

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
        backButton = new TextButton("Back to Menu", skin);

        Table bottomTable = new Table();
        bottomTable.add(createButton).padRight(10);
        bottomTable.add(refreshButton).padRight(10);
        bottomTable.add(backButton);
        mainTable.add(bottomTable).colspan(3).pad(20).row();

        setupEventListeners();

        refreshLobbyList();
        updateCurrentLobby();
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

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new MainMenuScreen(game));
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyId != null && isAdmin) {
//                    NetworkManager.startGame(currentLobbyId);
                }
            }
        });

        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLobbyId != null) {
//                    NetworkManager.leaveLobby(currentLobbyId);
                    currentLobbyId = null;
                    updateCurrentLobby();
                }
            }
        });
    }

    private void showCreateLobbyDialog() {
        Dialog dialog = new Dialog("Create New Lobby", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String name = getContentTable().findActor("name").getText();
                    boolean isPrivate = getContentTable().findActor("private").isChecked();
                    String password = getContentTable().findActor("password").getText();

                    if (isPrivate && password.isEmpty()) {
                        showError("Password is required for private lobbies");
                        return;
                    }

                    createLobby(name, isPrivate, password);
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

    private void createLobby(String name, boolean isPrivate, String password) {
        currentLobbyId = gameClient.ge.createLobby(name, isPrivate, password);
        if (currentLobbyId != null) {
            isAdmin = true;
            updateCurrentLobby();
            refreshLobbyList();
        } else {
            showError("Failed to create lobby");
        }
    }

    private void refreshLobbyList() {
        List<GameLobby> lobbies = NetworkManager.getPublicLobbies();
        lobbyListTable.clear();

        if (lobbies.isEmpty()) {
            lobbyListTable.add("No public lobbies available").pad(20);
            return;
        }

        lobbyListTable.add("Lobby Name").pad(10).width(150);
        lobbyListTable.add("Players").pad(10).width(80);
        lobbyListTable.add("Status").pad(10).width(100);
        lobbyListTable.add("").pad(10).width(100).row();
        lobbyListTable.add(new Separator()).colspan(4).fillX().padBottom(5).row();

        for (LobbyInfo lobby : lobbies) {
            if (lobby.getId().equals(currentLobbyId)) continue;

            lobbyListTable.add(lobby.getName()).pad(5).left();
            lobbyListTable.add(lobby.getPlayerCount() + "/4").pad(5);
            lobbyListTable.add(lobby.isPrivate() ? "Private" : "Public").pad(5);

            TextButton joinButton = new TextButton("Join", skin);
            joinButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    joinLobby(lobby);
                }
            });

            lobbyListTable.add(joinButton).pad(5).width(80);
            lobbyListTable.row();
        }
    }

    private void joinLobby(LobbyInfo lobby) {
        if (lobby.isPrivate()) {
            showPasswordDialog(lobby);
        } else {
            attemptJoinLobby(lobby.getId(), null);
        }
    }

    private void showPasswordDialog(LobbyInfo lobby) {
        Dialog dialog = new Dialog("Join Private Lobby", skin);

        Table content = dialog.getContentTable();
        content.pad(15);

        content.add(new Label("Enter password for:", skin)).colspan(2).row();
        content.add(new Label(lobby.getName(), skin, "subtitle")).colspan(2).padBottom(10).row();

        content.add(new Label("Password:", skin)).padRight(10);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        content.add(passwordField).width(200).row();

        dialog.button("Join", true);
        dialog.button("Cancel", false);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.equals(true)) {
                attemptJoinLobby(lobby.getId(), passwordField.getText());
            }
            return null;
        });

        dialog.show(stage);
    }

    private void attemptJoinLobby(String lobbyId, String password) {
        if (NetworkManager.joinLobby(lobbyId, password)) {
            currentLobbyId = lobbyId;
            isAdmin = false;
            updateCurrentLobby();
        } else {
            showError("Failed to join lobby. Wrong password or lobby is full.");
        }
    }

    private void updateCurrentLobby() {
        currentLobbyTable.clear();

        if (currentLobbyId == null) {
            currentLobbyTable.add("Not in any lobby").pad(20);
            startButton.setVisible(false);
            leaveButton.setVisible(false);
            return;
        }

        LobbyInfo lobby = NetworkManager.getLobbyInfo(currentLobbyId);
        if (lobby == null) {
            currentLobbyTable.add("Lobby information not available").pad(20);
            return;
        }

        currentLobbyTable.add(new Label("Lobby:", skin)).padRight(5);
        currentLobbyTable.add(lobby.getName()).left().width(150).padRight(20);

        currentLobbyTable.add(new Label("Players:", skin)).padRight(5);
        currentLobbyTable.add(lobby.getPlayerCount() + "/4").left().width(50).padRight(20);

        currentLobbyTable.add(new Label("Status:", skin)).padRight(5);
        currentLobbyTable.add(lobby.isPrivate() ? "Private" : "Public").left().row();

        currentLobbyTable.add(new Separator()).colspan(6).fillX().padVertical(5).row();

        for (String player : lobby.getPlayers()) {
            Label playerLabel = new Label(player, skin);
            if (player.equals(lobby.getAdminId())) {
                playerLabel.setText(player + " (Admin)");
                playerLabel.setColor(1, 0.8f, 0, 1);
            }
            currentLobbyTable.add(playerLabel).colspan(6).align(Align.left).pad(3).row();
        }

        startButton.setVisible(isAdmin);
        leaveButton.setVisible(true);
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
    @Override public void dispose() {
        stage.dispose();
    }
}
