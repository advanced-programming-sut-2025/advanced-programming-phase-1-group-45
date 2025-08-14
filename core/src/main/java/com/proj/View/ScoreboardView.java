package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Control.ScoreboardController;
import com.proj.Model.GameAssetManager;
import com.proj.Model.ScoreboardEntry;
import java.util.List;


public class ScoreboardView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final ScoreboardController controller;
    private Table scoreTable;
    private Runnable backCallback;
    private Texture backgroundTexture;
    private Image backgroundImage;


    public ScoreboardView(ScoreboardController controller) {
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        backgroundTexture = new Texture(Gdx.files.internal("menu_bg.png"));
        backgroundImage = new Image(backgroundTexture);
        setupUI();
        refreshScoreboard();
    }

    public void setBackCallback(Runnable callback) {
        this.backCallback = callback;
    }

    private void setupUI() {
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Title
        mainTable.add(new Label("Scoreboard", skin, "title")).padBottom(20).row();

        // Score table
        scoreTable = new Table(skin);
        scoreTable.defaults().pad(5);
        ScrollPane scrollPane = new ScrollPane(scoreTable, skin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).grow().row();

        // Buttons
        TextButton refreshButton = new TextButton("Refresh", skin);
        TextButton backButton = new TextButton("Back", skin);

        // In setupUI()
        scoreTable.add("Rank").width(80);
        scoreTable.add("Player").width(150);  // New column
        scoreTable.add("Money").width(100);
        scoreTable.add("Quest").width(80);
        scoreTable.add("Skill Level").width(100).row();

        refreshButton.addListener(e -> {
            if (e instanceof InputEvent && ((InputEvent)e).getType() == InputEvent.Type.touchDown) {
                refreshScoreboard();
                return true;
            }
            return false;
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (backCallback != null) {
                    Gdx.app.postRunnable(() -> backCallback.run());
                }
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(refreshButton).padRight(10);
        buttonTable.add(backButton);
        mainTable.add(buttonTable).padTop(20);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
    private void refreshScoreboard() {
        controller.loadScoreboard();
        scoreTable.clear();

        // Add headers with color styling
        Label rankHeader = new Label("Rank", skin);
        rankHeader.setColor(skin.getColor("stone"));
        scoreTable.add(rankHeader).width(80);

        Label playerHeader = new Label("Player", skin);
        playerHeader.setColor(skin.getColor("stone"));
        scoreTable.add(playerHeader).width(150);

        Label moneyHeader = new Label("Money", skin);
        moneyHeader.setColor(skin.getColor("stone"));
        scoreTable.add(moneyHeader).width(100);

        Label questHeader = new Label("Quest", skin);
        questHeader.setColor(skin.getColor("stone"));
        scoreTable.add(questHeader).width(80);

        Label skillHeader = new Label("Skill Level", skin);
        skillHeader.setColor(skin.getColor("stone"));
        scoreTable.add(skillHeader).width(100);
        scoreTable.row();

        List<ScoreboardEntry> entries = controller.getScoreboardEntries();
        for (int i = 0; i < entries.size(); i++) {
            ScoreboardEntry entry = entries.get(i);

            // Create rank label with appropriate styling
            Label rankLabel = new Label("", skin);
            if (i == 0) {
                rankLabel.setText("1st");
                rankLabel.setColor(skin.getColor("highlight"));
            } else if (i == 1) {
                rankLabel.setText("2nd");
                rankLabel.setColor(skin.getColor("field"));
            } else if (i == 2) {
                rankLabel.setText("3rd");
                rankLabel.setColor(skin.getColor("white"));
            } else {
                rankLabel.setText((i + 1) + "th");
            }

            // Create player name label
            Label nameLabel = new Label(entry.getPlayerName(), skin);
            if (i == 0) nameLabel.setColor(skin.getColor("highlight"));
            else if (i == 1) nameLabel.setColor(skin.getColor("field"));
            else if (i == 2) nameLabel.setColor(skin.getColor("white"));

            // Create money label
            Label moneyLabel = new Label(String.format("%,dg", entry.getMoney()), skin);
            moneyLabel.setColor(skin.getColor("white"));

            // Create quest label
            Label questLabel = new Label(String.valueOf(entry.getQuestsCompleted()), skin);
            questLabel.setColor(skin.getColor("white"));

            // Create skill level label
            Label skillLabel = new Label("Level " + entry.getSkillLevel(), skin);
            skillLabel.setColor(skin.getColor("field"));

            // Add all labels to the table
            scoreTable.add(rankLabel);
            scoreTable.add(nameLabel);
            scoreTable.add(moneyLabel);
            scoreTable.add(questLabel);
            scoreTable.add(skillLabel);
            scoreTable.row();
        }
    }
}
