package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.*;
import com.proj.Main;

//help: fatemeh
public class SignupMenuView implements Screen {
    private Stage stage;
    private final Label errorMessage;
    private final Label menuTitle;
    private final TextField usernameField;
    private final TextField passwordField;
    private final TextField securityQuestionField;
    private final TextButton signUpButton;
    private final TextButton guestButton;
    public Table table;
    private final SignupMenuController controller;
    private Image backgroundImage;


    public SignupMenuView(SignupMenuController controller, Skin skin) {
        this.controller = controller;
        this.table = new Table();
        this.menuTitle = new Label("20 minutes till dawn", skin);

        this.usernameField = new TextField("", skin);
        usernameField.setMessageText("enter Username");

        this.passwordField = new TextField("", skin);
        passwordField.setMessageText("enter Password");
        passwordField.setPasswordMode(true);

        this.securityQuestionField = new TextField("", skin);
        securityQuestionField.setMessageText("What is your dream job?");

        this.signUpButton = new TextButton("Sign Up", skin);
        this.guestButton = new TextButton("as Guest", skin);

        this.errorMessage = new Label("", skin);
        errorMessage.setColor(1, 0, 0, 1);
        backgroundImage = new Image(Main.menuBackground);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        table.row().pad(10, 0, 10, 0);
        table.add(errorMessage);
        table.row().pad(10, 0, 10, 0);
        table.add(menuTitle);
        table.row().pad(10, 0, 10, 0);
        table.add(usernameField).width(400);
        table.row().pad(10, 0, 10, 0);
        table.add(passwordField).width(400);
        table.row().pad(10, 0, 10, 0);
        table.add(securityQuestionField).width(500);
        table.row().pad(10, 0, 10, 0);
        table.add(signUpButton).width(300);
        table.row().pad(10, 0, 10, 0);
        table.add(guestButton).width(300);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        stage.addActor(table);

        controller.handleSignupMenuButton();
        controller.handleGuestButton();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 0.7f, 0.7f, 1);
        Main.getBatch().begin();
        Main.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    public Stage getStage() { return stage; }
    public TextField getUsername() { return usernameField; }
    public TextField getPassword() { return passwordField; }
    public TextField getSecurityQuestion() { return securityQuestionField; }
    public TextButton getSignUpButton() { return signUpButton; }
    public TextButton getGuestButton() { return guestButton; }
    public Label getErrorMessage() { return errorMessage; }
}
