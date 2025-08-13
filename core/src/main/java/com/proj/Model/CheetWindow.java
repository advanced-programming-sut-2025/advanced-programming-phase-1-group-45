package com.proj.Model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.proj.Main;

public class CheetWindow {
    private final Stage stage;
    private final Skin skin;
    private final Main main;
    private Window envWindow;
    private boolean isVisible = false;

    // دکمه‌های آب و هوا
    private ImageButton sunnyBtn;
    private ImageButton rainyBtn;
    private ImageButton cloudyBtn;
    private ImageButton snowyBtn;

    public CheetWindow(Main main, Stage stage) {
        this.main = main;
        this.stage = stage;
        this.skin = GameAssetManager.getGameAssetManager().getStardewSkin();
        createUI();
    }

    private void createUI() {
        envWindow = new Window("CheetCodes", skin);
        envWindow.setSize(350, 200);
        envWindow.setPosition(650, 20); // موقعیت متفاوت از پنجره چت
        envWindow.setMovable(true);
        envWindow.setResizable(false);

        Table contentTable = new Table();
        contentTable.pad(15);
        contentTable.defaults().pad(5);

        // دکمه تنظیم زمان
        Texture timeIcon = new Texture("assets/time_icon.png"); // آیکون ساعت
        ImageButton timeButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(timeIcon)));
        timeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showTimeInputDialog();
            }
        });

        contentTable.add(timeButton).size(64, 64).padBottom(15).row();
        contentTable.add(new Label("Set Time", skin)).row();

        // جدول دکمه‌های آب و هوا
        Table weatherTable = new Table();
        weatherTable.defaults().size(48, 48).pad(5);

        sunnyBtn = createWeatherButton("assets/sunny.png", "SUNNY");
        rainyBtn = createWeatherButton("assets/rainy.png", "RAINY");
        cloudyBtn = createWeatherButton("assets/cloudy.png", "CLOUDY");
        snowyBtn = createWeatherButton("assets/snowy.png", "SNOWY");

        weatherTable.add(sunnyBtn);
        weatherTable.add(rainyBtn);
        weatherTable.add(cloudyBtn);
        weatherTable.add(snowyBtn);

        contentTable.add(weatherTable).padTop(10);
        envWindow.add(contentTable);

        envWindow.setVisible(false);
        stage.addActor(envWindow);
    }

    private ImageButton createWeatherButton(String texturePath, final String weatherType) {
        Texture texture = new Texture(texturePath);
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                main.setWeather(weatherType); // فراخوانی متد تغییر آب و هوا در Main
            }
        });
        return button;
    }

    private void showTimeInputDialog() {
        Dialog dialog = new Dialog("Set Time", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) { // OK pressed
                    TextField timeField = (TextField) getContentTable().findActor("timeInput");
                    String time = timeField.getText();
                    if (time.matches("^\\d{1,2}:\\d{2}$")) {
                        main.setGameTime(time); // فراخوانی متد تغییر زمان در Main
                    } else {
                        // نمایش خطا
                        new Dialog("Error", skin).text("Invalid time format! Use HH:mm").button("OK").show(stage);
                    }
                }
            }
        };

        dialog.text("Enter time (HH:mm):");
        TextField timeInput = new TextField("", skin);
        timeInput.setName("timeInput");
        dialog.getContentTable().add(timeInput).width(150).padTop(10);
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.key(Input.Keys.ENTER, true); // Enter برای تایید
        dialog.show(stage);
    }

    public void toggle() {
        isVisible = !isVisible;
        envWindow.setVisible(isVisible);
        envWindow.toFront(); // نمایش پنجره در جلو
    }

    public boolean isVisible() {
        return isVisible;
    }
}
