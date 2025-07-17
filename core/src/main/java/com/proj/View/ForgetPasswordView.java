package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.ForgetPasswordController;

public class ForgetPasswordView implements Screen {
    private final ForgetPasswordController controller;

    private final Table table = new Table();
    private Stage stage;

    private final Label title;
    private final Label questionLabel;
    private final Label errorLabel;
    private final TextField usernameField;
    private final TextField answerField;
    private final TextField newPasswordField;
    private final TextButton checkButton;
    private final TextButton resetButton;

    public ForgetPasswordView(ForgetPasswordController controller, Skin skin) {
        this.controller = controller;
        controller.setView(this);

        title = new Label("Forgot Password", skin);
        questionLabel = new Label("", skin);
        errorLabel = new Label("", skin);
        errorLabel.setColor(1,0,0,1);

        usernameField = new TextField("", skin); usernameField.setMessageText("Username");
        answerField = new TextField("", skin);  answerField.setMessageText("Answer security question");
        newPasswordField = new TextField("", skin);  newPasswordField.setMessageText("New Password");

        checkButton = new TextButton("Check", skin);
        resetButton = new TextButton("Reset", skin);
        resetButton.setDisabled(true);
    }

    @Override public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        table.add(title).pad(10);
        table.row();
        table.add(errorLabel).pad(5);
        table.row();
        table.add(usernameField).width(400).pad(5);
        table.row();
        table.add(checkButton).width(200).pad(5);
        table.row();
        table.add(questionLabel).pad(5);
        table.row();
        table.add(answerField).width(450).pad(5);
        table.row();
        table.add(newPasswordField).width(400).pad(5);
        table.row();
        table.add(resetButton).width(200).pad(5);

        stage.addActor(table);

        checkButton.addListener(e -> {
            controller.handleCheck();
            return true;
        });
        resetButton.addListener(e -> {
            controller.handleReset();
            return true;
        });
    }

    @Override public void render(float delta) {
        ScreenUtils.clear(0.25f,0.45f,0.55f,1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(),1/30f));
        stage.draw();
    }

    @Override public void resize(int w,int h){} @Override public void pause(){}
    @Override public void resume(){} @Override public void hide(){} @Override public void dispose(){}

    public TextField getUsernameField(){ return usernameField; }
    public Label getQuestionLabel(){ return questionLabel; }
    public TextField getAnswerField(){ return answerField; }
    public TextField getNewPasswordField(){ return newPasswordField; }
    public Label getErrorLabel(){ return errorLabel; }
    public TextButton getResetButton(){ return resetButton; }
    public TextButton getCheckButton() { return checkButton; }
}

