package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.ChangeInfoController;

public class ChangeInfoMenuView implements Screen {
    private Stage stage;
    private TextField usernameField;
    private TextField nicknameField;
    private TextField passwordField;
    private TextButton saveButton;
    private final ChangeInfoController controller;

    // Constructor that matches your pattern
    public ChangeInfoMenuView(ChangeInfoController controller, Skin skin) {
        this.controller = controller;
        stage = new Stage(new ScreenViewport());

        // Create UI components
        usernameField = new TextField("new username", skin);
        nicknameField = new TextField("new nickname", skin);
        passwordField = new TextField("new password", skin);
        passwordField.setPasswordMode(true);
        saveButton = new TextButton("Save Changes", skin);

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        table.add(usernameField).width(300).pad(10).row();
        table.add(nicknameField).width(300).pad(10).row();
        table.add(passwordField).width(300).pad(10).row();
        table.add(saveButton).pad(20);

        stage.addActor(table);

        // Set up event handlers
        controller.setView(this);
    }

    // Getters for controller
    public TextField getUsernameField() { return usernameField; }
    public TextField getNicknameField() { return nicknameField; }
    public TextField getPasswordField() { return passwordField; }
    public TextButton getSaveButton() { return saveButton; }

    // Screen interface methods
    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
