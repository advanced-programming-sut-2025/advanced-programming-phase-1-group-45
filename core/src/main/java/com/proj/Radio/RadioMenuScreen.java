package com.proj.Radio;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.proj.GameScreen;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.map.farmName;

import java.io.File;

public class RadioMenuScreen implements Screen {
    private final Main main;
    private Stage stage;
    private Skin skin;
    //private Array<Music> tracks = new Array<>();
    private int currentTrackIndex = -1;
    private TextButton playButton, stopButton, nextButton, prevButton, addButton, connectButton, backButton;
    private Label statusLabel, titleLabel, trackNameLabel;
    private ProgressBar progressBar;
    private Table trackListTable;
    private ScrollPane trackScrollPane;
    private RadioNetwork network;
    private boolean isPlaying = false;
    private Array<TrackWrapper> tracks = new Array<>();
    private GameScreen gameScreen;

    public RadioMenuScreen(Main main, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage();
        skin = skin = GameAssetManager.getGameAssetManager().getSkin();
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("Radio.png"))));

        titleLabel = new Label("RADIO STATION", skin, "title");
        mainTable.add(titleLabel).padTop(20).colspan(3).row();

        trackNameLabel = new Label("No track selected", skin);
        mainTable.add(trackNameLabel).colspan(3).padTop(10).row();

        progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue(0);
        mainTable.add(progressBar).width(300).padTop(10).colspan(3).row();

        Table controlTable = new Table();
        prevButton = new TextButton("<<", skin);
        playButton = new TextButton("Play", skin);
        stopButton = new TextButton("Stop", skin);
        nextButton = new TextButton(">>", skin);

        controlTable.add(prevButton).pad(10);
        controlTable.add(playButton).pad(10);
        controlTable.add(stopButton).pad(10);
        controlTable.add(nextButton).pad(10);

        mainTable.add(controlTable).padTop(20).colspan(3).row();

        trackListTable = new Table();
        trackScrollPane = new ScrollPane(trackListTable, skin);
        trackScrollPane.setFadeScrollBars(false);
        mainTable.add(trackScrollPane).width(350).height(150).padTop(20).colspan(3).row();

        Table actionTable = new Table();
        addButton = new TextButton("Add Track", skin);
        connectButton = new TextButton("Connect to Friend", skin);
        backButton = new TextButton("Back", skin);

        actionTable.add(addButton).pad(10);
        actionTable.add(connectButton).pad(10);
        actionTable.add(backButton).pad(10);

        mainTable.add(actionTable).padTop(10).colspan(3).row();

        statusLabel = new Label("Ready", skin);
        mainTable.add(statusLabel).padTop(10).colspan(3).row();

        stage.addActor(mainTable);
        addButtonListeners();
    }

    private void addButtonListeners() {
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                    try {
                        AudioFileDialog dialog = new AudioFileDialog(Gdx.graphics);
                        dialog.setTitle("Select Music Files");
                        dialog.setMultipleMode(true);
                        dialog.setFilenameFilter((filePath, name) ->
                            name.endsWith(".mp3") || name.endsWith(".ogg") || name.endsWith(".wav"));
                        dialog.show();

                        String[] files = dialog.getFiles();
                        if (files != null && files.length > 0) {
                            for (String file : files) {
                                addTrack(file);
                            }
                        }
                    } catch (Exception e) {
                        statusLabel.setText("Error opening file dialog");
                    }
                } else {
                    statusLabel.setText("File adding only supported on desktop");
                }
            }
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playCurrentTrack();
            }
        });

        stopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopCurrentTrack();
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextTrack();
            }
        });

        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previousTrack();
            }
        });

        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextInputDialog dialog = new TextInputDialog("Connect to Friend", "Enter IP Address:", skin);
                dialog.show(stage);
                dialog.setListener(ip -> {
                    try {
                        if (network != null) network.close();
                        network = new RadioNetwork(RadioMenuScreen.this);
                        network.connect(ip, 12345);
                        statusLabel.setText("Connected to: " + ip);
                    } catch (Exception e) {
                        statusLabel.setText("Connection failed: " + e.getMessage());
                    }
                });
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Use the Main's gameScreen reference
                if (main.getGameScreen() != null) {
                    main.setScreen(main.getGameScreen());
                } else {
                    // Fallback to creating new GameScreen if reference is null
                    main.setScreen(new GameScreen(main, farmName.STANDARD));
                }

                // Music handling
                if (currentTrackIndex >= 0 && currentTrackIndex < tracks.size) {
                    main.playMusic(tracks.get(currentTrackIndex).getMusic());
                }
            }
        });
    }

    private void nextTrack() {
        if (tracks.size == 0) return;
        stopCurrentTrack();
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size;
        playCurrentTrack();
    }

    private void addTrack(String filePath) {
        try {
            File file = new File(filePath);
            Music music = Gdx.audio.newMusic(Gdx.files.absolute(filePath));
            tracks.add(new TrackWrapper(music, file));
            updateTrackList();

            if (currentTrackIndex == -1) currentTrackIndex = 0;
            statusLabel.setText("Added: " + file.getName());
        } catch (Exception e) {
            statusLabel.setText("Error loading file: " + e.getMessage());
        }
    }

    private void updateTrackList() {
        trackListTable.clear();
        for (int i = 0; i < tracks.size; i++) {
            final int index = i;
            TrackWrapper track = tracks.get(i);
            TextButton trackBtn = new TextButton(
                (i + 1) + ". " + track.getFileName(),
                skin
            );
            trackBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentTrackIndex = index;
                    playCurrentTrack();
                }
            });
            trackListTable.add(trackBtn).width(320).pad(5).row();
        }
    }


    private void stopCurrentTrack() {
        if (tracks.size == 0 || currentTrackIndex < 0 || currentTrackIndex >= tracks.size) return;

        TrackWrapper trackWrapper = tracks.get(currentTrackIndex);
        Music current = trackWrapper.getMusic();
        current.stop();
        isPlaying = false;
        trackNameLabel.setText("Playback Stopped");

        if (network != null) {
            network.sendStopCommand();
        }
    }

    private void previousTrack() {
        if (tracks.size == 0) return;
        stopCurrentTrack();
        currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size;
        playCurrentTrack();
    }


    public void stopTrack() {
        if (currentTrackIndex >= 0 && currentTrackIndex < tracks.size) {
            TrackWrapper trackWrapper = tracks.get(currentTrackIndex);
            Music track = trackWrapper.getMusic();
            track.stop();
        }
        isPlaying = false;
        trackNameLabel.setText("Remote Stopped");
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    private void playCurrentTrack() {
        if (tracks.size == 0) return;
        if (currentTrackIndex < 0 || currentTrackIndex >= tracks.size) {
            currentTrackIndex = 0;
        }

        TrackWrapper trackWrapper = tracks.get(currentTrackIndex);
        main.playMusic(trackWrapper.getMusic());

        trackNameLabel.setText("Now Playing: " + trackWrapper.getFileName());
        isPlaying = true;

        if (network != null) {
            network.sendPlayCommand(currentTrackIndex, 0);
        }
    }


    public void playTrack(int index, float position) {
        if (index < 0 || index >= tracks.size) return;

        currentTrackIndex = index;
        TrackWrapper trackWrapper = tracks.get(index);
        Music track = trackWrapper.getMusic();
        track.play();
        track.setPosition(position);

        trackNameLabel.setText("Remote Playing: " + trackWrapper.getFileName());
        isPlaying = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        for (TrackWrapper track : tracks) {
            track.getMusic().dispose();
        }
        if (network != null) {
            network.close();
        }
    }
}
