package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.LoginMenuController;
import com.proj.Main;

public class LoginMenuView implements Screen {
    private Stage stage;
    private final Table table;
    private final Label errorMessage;
    private final Label menuTitle;
    private final TextField username;
    private final TextField password;
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
        username = new TextField("", skin);
        username.setMessageText("Username");

        password = new TextField("", skin);
        password.setMessageText("Password");
        password.setPasswordMode(true);

        loginButton = new TextButton("Login", skin);
        forgotPasswordButton = new TextButton("Forget Password?", skin);

        errorMessage = new Label("", skin);
        errorMessage.setColor(1, 0, 0, 1);
        backgroundImage = new Image(Main.menuBackground);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.row().pad(10, 0, 10, 0);
        table.add(errorMessage);
        table.row().pad(10);
        table.add(menuTitle);
        table.row().pad(10);
        table.add(username).width(300);
        table.row().pad(10);
        table.add(password).width(300);
        table.row().pad(10);
        table.add(loginButton);
        table.row().pad(10);
        table.add(forgotPasswordButton);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.7f, 0.6f, 0.7f, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        controller.handleLoginButton();
        controller.handleForgetPasswordButton();
    }

    public TextField getUsername() { return username; }
    public TextField getPassword() { return password; }
    public TextButton getLoginButton() { return loginButton; }
    public TextButton getForgotPasswordButton() { return forgotPasswordButton; }
    public Label getErrorMessage() { return errorMessage; }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

}
