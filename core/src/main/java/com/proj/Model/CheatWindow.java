package com.proj.Model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.proj.Main;

public class CheatWindow {
    private final Stage stage;
    private final Skin skin;
    private final Main main;
    private Window envWindow;
    private boolean isVisible = false;

    private ImageButton sunnyBtn;
    private ImageButton rainyBtn;
    private ImageButton stormyBtn;
    private ImageButton snowyBtn;

    private String currentWeather = "SUNNY"; // مقدار پیش‌فرض

    public CheatWindow(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
        this.skin = GameAssetManager.getGameAssetManager().getStardewSkin();
        createUI();
    }

    private void createUI() {
        envWindow = new Window("CheatCodes", skin);
        envWindow.setSize(700, 400);
        envWindow.setPosition(100, 80);
        envWindow.setMovable(true);
        envWindow.setResizable(false);

        Table contentTable = new Table();
        contentTable.pad(15);
        contentTable.defaults().pad(5);

        Texture timeIcon = new Texture("assets/time_icon.png");
        ImageButton timeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(timeIcon)));
        timeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTimeInputDialog();
            }
        });
        contentTable.add(timeButton).size(100, 100).padBottom(15);
        Texture dayNightIcon = new Texture("assets/day_and_night_icon.png");
        ImageButton dayButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(dayNightIcon)));
        dayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDayInputDialog();
            }
        });
        contentTable.add(dayButton).size(100, 100).padBottom(15).padLeft(5).row();

        Table weatherTable = new Table();
        weatherTable.defaults().size(100, 100).pad(5);

        sunnyBtn = createWeatherButton(GameAssetManager.getGameAssetManager().getSunIcon(), "SUNNY");
        rainyBtn = createWeatherButton(GameAssetManager.getGameAssetManager().getRainIcon(), "RAINY");
        stormyBtn = createWeatherButton(GameAssetManager.getGameAssetManager().getStormIcon(), "STORMY");
        snowyBtn = createWeatherButton(GameAssetManager.getGameAssetManager().getSnowIcon(), "SNOWY");

        weatherTable.add(sunnyBtn);
        weatherTable.add(rainyBtn);
        weatherTable.add(stormyBtn);
        weatherTable.add(snowyBtn);

        contentTable.add(weatherTable).padTop(10);
        envWindow.add(contentTable);
        updateWeatherButtons();

        envWindow.setVisible(false);
        stage.addActor(envWindow);
    }
    private void updateWeatherButtons() {
        updateButtonStyle(sunnyBtn, "SUNNY");
        updateButtonStyle(rainyBtn, "RAINY");
        updateButtonStyle(stormyBtn, "STORMY");
        updateButtonStyle(snowyBtn, "SNOWY");
    }
    private void updateButtonStyle(ImageButton button, String weatherType) {
        button.setColor(Color.WHITE);

        if (weatherType.equals(currentWeather)) {
            button.setColor(Color.PINK);
        }
    }

    public void setCurrentWeather(String weather) {
        this.currentWeather = weather;
        updateWeatherButtons();
    }

    private ImageButton createWeatherButton(Texture texture1, final String weatherType) {
        Texture texture = texture1;
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setCurrentWeather(weatherType);
                Main.getMain().getGameClient().changeWeather(weatherType.toLowerCase());
                toggle();
            }
        });
        button.getImage().setColor(1, 1, 1, 0.8f);

        return button;
    }

    private void showTimeInputDialog() {
        Dialog dialog = new Dialog("Set Time", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) { // OK pressed
                    TextField timeField = (TextField) getContentTable().findActor("timeInput");
                    String time = timeField.getText();
                    if (time.matches("^\\d+$")) {
                        Main.getMain().getGameClient().changeHour(Integer.parseInt(time));
                    } else {
                        new Dialog("Error", skin).text("Invalid time format! Use HH:mm").button("OK").show(stage);
                    }
                }
            }
        };

        dialog.text("Enter hour: ");
        TextField timeInput = new TextField("", skin);
        timeInput.setName("timeInput");
        dialog.getContentTable().add(timeInput).width(150).padTop(10);
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.show(stage);
    }

    private void showDayInputDialog() {
        Dialog dialog = new Dialog("Set Day", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) { // OK pressed
                    TextField timeField = (TextField) getContentTable().findActor("dayInput");
                    String day = timeField.getText();
                    if (day.matches("^\\d+$")) {

                        Main.getMain().getGameClient().changeDay(Integer.parseInt(day));
                    } else {
                        new Dialog("Error", skin).text("Invalid day format! Use numbers").button("OK").show(stage);
                    }
                }
            }
        };

        dialog.text("Enter day: ");
        TextField dayInput = new TextField("", skin);
        dayInput.setName("dayInput");
        dialog.getContentTable().add(dayInput).width(150).padTop(10);
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true);
        dialog.show(stage);
    }

    public void toggle() {
        isVisible = !isVisible;
        envWindow.setVisible(isVisible);
        envWindow.toFront();
    }

    public boolean isVisible() {
        return isVisible;
    }
}
