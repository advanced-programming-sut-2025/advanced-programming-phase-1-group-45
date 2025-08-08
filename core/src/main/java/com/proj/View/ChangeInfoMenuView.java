package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.ChangeInfoController;
import com.proj.Model.User; 
import com.proj.Session;

public class ChangeInfoMenuView implements Screen {
    private Stage stage;
    private TextField usernameField;
    private TextField nicknameField;
    private TextField passwordField;
    private TextButton saveButton;
    private TextField emailField;
    private final ChangeInfoController controller;
    private TextButton generatePasswordButton;
    private Label errorLabel;

    // Constructor that matches your pattern
    public ChangeInfoMenuView(ChangeInfoController controller, Skin skin) {
        this.controller = controller;
        stage = new Stage(new ScreenViewport());

        // Create UI components
        usernameField = new TextField("new username", skin);
        nicknameField = new TextField("new nickname", skin);
        passwordField = new TextField("new password", skin);
        emailField = new TextField("new email", skin);
        passwordField.setPasswordMode(true);
        saveButton = new TextButton("Save Changes", skin);
        generatePasswordButton = new TextButton("Generate Password", skin);
        errorLabel = new Label("", skin);
        errorLabel.setColor(1, 0, 0, 1);

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        table.add(usernameField).width(300).pad(10).row();
        table.add(nicknameField).width(300).pad(10).row();
        table.add(passwordField).width(300).pad(10).row();
        table.add(emailField).width(300).pad(10).row();
        table.add(saveButton).pad(20);

        Table buttonTable = new Table();
        buttonTable.add(generatePasswordButton).padRight(10); // Add generate button first
        buttonTable.add(saveButton);

        TextButton showPasswordButton = new TextButton("Show", skin);
        showPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                passwordField.setPasswordMode(!passwordField.isPasswordMode());
            }
        });

        table.add(buttonTable).pad(20);

        stage.addActor(table);

        // Set up event handlers
        controller.setView(this);
    }

    // Getters for controller
    public TextField getUsernameField() { return usernameField; }
    public TextField getNicknameField() { return nicknameField; }
    public TextField getPasswordField() { return passwordField; }
    public TextButton getSaveButton() { return saveButton; }
    public TextField getEmailField() { return emailField; } // Added email getter
    public TextButton getGeneratePasswordButton() { return generatePasswordButton; }

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
