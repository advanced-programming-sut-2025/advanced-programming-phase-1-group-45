package com.proj;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.proj.Control.SignupMenuController;
import com.proj.Model.GameAssetManager;
import com.proj.View.SignupMenuView;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private static Main main;
    private static SpriteBatch batch;
    private Music backgroundMusic;
    private static final String PREFS_NAME = "StardewMiniSettings";
    private static final String MUSIC_VOLUME_KEY = "volume";
    private static final float DEFAULT_VOLUME = 0.5f;
    public static Texture menuBackground;
    /*@Override
    public void create() {
        main = this;
        batch = new SpriteBatch();

        loadMusic();
        menuBackground = new Texture(Gdx.files.internal("menu_bg.png"));
        setScreen(new GameScreen());

        //main.setScreen(new SignupMenuView(new SignupMenuController(),
            //GameAssetManager.getGameAssetManager().getSkin()));
    }*/
    @Override
    public void create() {
        main = this;
        batch = new SpriteBatch();
        menuBackground = new Texture(Gdx.files.internal("menu_bg.png"));

        showAuthScreen();
        //setScreen(new GameScreen());

        loadMusic();
    }

    public void startNewGame() {// in jadide
        if (getScreen() != null) {
            getScreen().dispose();
        }
        setScreen(new GameScreen());
    }

    public void showAuthScreen() {
        setScreen(new SignupMenuView(
            new SignupMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()
        ));
    }

    public void switchToGameScreen() {
        Gdx.app.postRunnable(() -> {
            Screen current = getScreen();
            setScreen(new GameScreen());
            if (current != null) {
                current.dispose();
            }
            changeBackgroundMusic("music/theme2.mp3");
        });
    }

    public void changeBackgroundMusic(String filePath) {
        // Stop and dispose current music if exists
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        backgroundMusic.setVolume(loadMusicVolume());
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    /*@Override
    public void setScreen(Screen screen) {
        // Dispose previous screen to save memory
        Screen previous = getScreen();
        super.setScreen(screen);
        if (previous != null) {
            previous.dispose();
        }
    }*/
    @Override
    public void setScreen(Screen screen) {
        Screen previous = getScreen();
        super.setScreen(screen);
        if (previous != null && previous != screen) {
            previous.dispose();
        }
    }

    public void loadMusic() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/theme1.mp3"));

        float savedVolume = loadMusicVolume();
        assert backgroundMusic != null;
        backgroundMusic.setVolume(savedVolume);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }

    public static Main getMain() {
        return main;
    }

    public static void setMain(Main main) {
        Main.main = main;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static void setBatch(SpriteBatch batch) {
        Main.batch = batch;
    }

    public void setMusicVolume(float value) {
        backgroundMusic.setVolume(value);
    }

    private float loadMusicVolume() {
        return Gdx.app.getPreferences(PREFS_NAME).getFloat(MUSIC_VOLUME_KEY, DEFAULT_VOLUME);
    }
    public Music getBackgroundMusic() {
        return backgroundMusic;
    }
}
