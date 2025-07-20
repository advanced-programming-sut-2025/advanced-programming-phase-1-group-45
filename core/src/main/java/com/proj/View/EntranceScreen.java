package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.proj.Control.SignupMenuController;
import com.proj.Main;
import com.proj.Model.GameAssetManager;

public class EntranceScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture logoTexture;
    private Image backgroundImage;
    private Image logoImage;
    private float displayTime = 5.0f; // Show entrance screen for 3 seconds

    public EntranceScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Load textures
        backgroundTexture = new Texture(Gdx.files.internal("menu_bg.png"));
        logoTexture = new Texture(Gdx.files.internal("yellowLettersLogo.png"));

        // Create images
        backgroundImage = new Image(backgroundTexture);
        logoImage = new Image(logoTexture);

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);

        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        table.add(logoImage).center().padBottom(100);
        stage.addActor(table);

        // Schedule transition to signup menu
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Get skin from GameAssetManager
                Skin skin = GameAssetManager.getGameAssetManager().getSkin();
                game.setScreen(new SignupMenuView(new SignupMenuController(), skin));
            }
        }, displayTime);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        logoTexture.dispose();
    }
}
