package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.MainMenuController;
import com.proj.Main;

public class MainMenuView implements Screen {
    private Stage stage;
    private final Table table;
    private final MainMenuController controller;

    // UI Elements
    private final Label titleLabel;
    private final TextButton profileButton;
    private final TextButton newGameButton;
    private final TextButton loadGameButton;
    private final TextButton exitButton;
    private Image backgroundImage;
    private final TextButton backButton;

    public MainMenuView(MainMenuController controller, Skin skin) {
        this.controller = controller;
        this.table = new Table();

        // Create UI elements
        this.titleLabel = new Label("Main menu", skin, "title");
        this.profileButton = new TextButton("Profile", skin);
        this.newGameButton = new TextButton("change info", skin);
        this.loadGameButton = new TextButton("Load Last Game", skin);
        this.exitButton = new TextButton("Exit", skin);
        this.backButton = new TextButton("Back", skin);

        backgroundImage = new Image(Main.menuBackground);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        // Add elements to table
        table.add(titleLabel).padBottom(40).row();
        table.add(profileButton).width(420).padBottom(20).row();
        table.add(newGameButton).width(420).padBottom(20).row();
        table.add(loadGameButton).width(420).padBottom(20).row();
        table.add(exitButton).width(420);
        table.add(backButton).width(420).padBottom(20).row();

        // Set up background
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        stage.addActor(table);

        // Set up button listeners
        controller.handleProfileButton();
        controller.handleNewGameButton();
        controller.handleLoadGameButton();
        controller.handleExitButton();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }

    // Getters for buttons
    public TextButton getProfileButton() { return profileButton; }
    public TextButton getNewGameButton() { return newGameButton; }
    public TextButton getLoadGameButton() { return loadGameButton; }
    public TextButton getExitButton() { return exitButton; }
}
