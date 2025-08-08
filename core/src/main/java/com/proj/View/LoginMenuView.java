package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.LoginMenuController;
import com.proj.Main;
import com.proj.network.client.NetworkEventListener;
import com.proj.network.event.NetworkEvent;

public class LoginMenuView implements Screen {
    private Stage stage;
    private final Table table;
    private final Label errorMessage;
    private final Label menuTitle;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextButton loginButton;
    private final TextButton forgotPasswordButton;
    private Image backgroundImage;
    private final LoginMenuController controller;

    public LoginMenuView(LoginMenuController controller, Skin skin) {
        this.controller = controller;
        controller.setView(this);

        table = new Table();
        table.setFillParent(true);
        table.center();

        menuTitle = new Label("Login Menu", skin);
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username or Email");  // Updated hint text

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);

        loginButton = new TextButton("Login", skin);
        forgotPasswordButton = new TextButton("Forgot Password?", skin);

        errorMessage = new Label("", skin);
        errorMessage.setColor(1, 0, 0, 1);  // Red color for errors
        backgroundImage = new Image(Main.menuBackground);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Clear existing actors if any
        stage.clear();

        // Build UI layout
        table.clear();
        table.row().pad(10, 0, 10, 0);
        table.add(errorMessage).colspan(2);
        table.row().pad(10);
        table.add(menuTitle).colspan(2);
        table.row().pad(10);
        //table.add(new Label("Username/Email:", skin)).right().padRight(10);
        table.add(usernameField).width(300);
        table.row().pad(10);
        //table.add(new Label("Password:", skin)).right().padRight(10);
        table.add(passwordField).width(300);
        table.row().pad(15);
        table.add(loginButton).colspan(2).width(150);
        table.row().pad(5);
        table.add(forgotPasswordButton).colspan(2);

        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        stage.addActor(table);

        // Setup event handlers
        controller.handleLoginButton();
        controller.handleForgetPasswordButton();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.7f, 0.6f, 0.7f, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    // Getters
    public TextField getUsername() { return usernameField; }
    public TextField getPassword() { return passwordField; }
    public TextButton getLoginButton() { return loginButton; }
    public TextButton getForgotPasswordButton() { return forgotPasswordButton; }
    public Label getErrorMessage() { return errorMessage; }

    // Other lifecycle methods
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    @Override public void dispose() {
        stage.dispose();
    }
}
