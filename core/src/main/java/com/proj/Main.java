package com.proj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.Control.SignupMenuController;
import com.proj.Model.Cooking.Refrigerator;
import com.proj.Model.GameAssetManager;
import com.proj.View.SignupMenuView;
import com.proj.View.EntranceScreen;
import com.proj.map.farmName;
import com.proj.network.event.NetworkEvent;
import com.proj.network.client.GameClient;
import com.proj.network.client.NetworkEventListener;
import com.proj.Database.DatabaseHelper;
import com.proj.network.lobby.LobbyScreen;
import com.badlogic.gdx.graphics.GL20;
import com.proj.network.multiplayerGame.MultiplayerGameScreen;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game implements NetworkEventListener {
    private GameClient gameClient;
    private static Main main;
    private static SpriteBatch batch;
    private Music backgroundMusic;
    private static final String PREFS_NAME = "StardewMiniSettings";
    private static final String MUSIC_VOLUME_KEY = "volume";
    private static final float DEFAULT_VOLUME = 0.5f;
    public static Texture menuBackground;
    public GameScreen gameScreen;
    public Refrigerator refrigerator;

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private DatabaseHelper dbHelper;
    private LobbyScreen lobbyScreen;
    private Music currentMusic;

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
        Gdx.app.log("OpenGL", "Version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
        Gdx.app.log("OpenGL", "Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));
        Gdx.app.log("OpenGL", "Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
        main = this;
        batch = new SpriteBatch();
        menuBackground = new Texture(Gdx.files.internal("menu_bg.png"));
        dbHelper = new DatabaseHelper();
        dbHelper.connect();
        Session.initialize(dbHelper);

        //showAuthScreen();
        //setScreen(new GameScreen());
        initializeNetworkClient();
        setScreen(new EntranceScreen(this));

        loadMusic();
    }

    public void playMusic(Music music) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = music;
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
//ino comment kardam chon nemidoonestam parametr avali ro chi bedam*******************
    /*public void startNewGame() {// in jadide
        if (getScreen() != null) {
            getScreen().dispose();
        }
        setScreen(new GameScreen(farmName.STANDARD));
    }*/

    public void showAuthScreen() {
        setScreen(new SignupMenuView(
            new SignupMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()
        ));
    }

    public void switchToGameScreen(farmName farm) {
        Gdx.app.postRunnable(() -> {
            Screen current = getScreen();
            setScreen(new GameScreen(this, farm));
            if (current != null) {
                current.dispose();
            }
            changeBackgroundMusic("music/theme2.mp3");
        });
    }

    public void switchToMultiplayerGameScreen(farmName farm) {
        Gdx.app.postRunnable(() -> {
            Screen current = getScreen();
            setScreen(new MultiplayerGameScreen(this, farm));
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
    public LobbyScreen getLobbyScreen() {
        lobbyScreen = new LobbyScreen(this);
        return lobbyScreen;
    }

    public void goToLobbyScreen() {
        setScreen(getLobbyScreen());
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

    private void initializeNetworkClient() {
        gameClient = new GameClient(SERVER_ADDRESS, SERVER_PORT);
//        gameClient.connect();
        gameClient.addNetworkListener(this);
    }
    @Override
    public void handleNetworkEvent (NetworkEvent event){
        switch (event.getType()) {
            case CONNECTED:
                Gdx.app.log("NETWORK", "CONNECTED TO SERVER");
                break;
            case DISCONNECTED:
                Gdx.app.log("NETWORK", "DISCONNECTED FROM SERVER");
                break;
            case AUTH_SUCCESS:
                handleAuthSuccess();
                break;
            case AUTH_FAILED:
                Gdx.app.log("NETWORK", "AUTH FAILED: " + event.getMessage());
                break;
            case ERROR:
                Gdx.app.log("NETWORK", "ERROR: " + event.getMessage());
                break;

            case PRIVATE_MESSAGE:
                if (getScreen() instanceof GameScreen) {
                    String[] parts = event.getMessage().split(":", 2);
                    if (parts.length == 2) {
                        String sender = parts[0].trim();
                        String message = parts[1].trim();
                        ((GameScreen) getScreen()).getChatSystem().receiveMessage(sender, message, true);
                    }
                }
                break;

            case SYSTEM_MESSAGE:
                if (getScreen() instanceof GameScreen) {
                    ((GameScreen) getScreen()).getChatSystem().addSystemMessage(event.getMessage());
                }
                break;
        }
    }

    public void loginClient(String username, String password) {
        gameClient.login(username, password);
    }

    private void handleAuthSuccess() {
//        Gdx.app.postRunnable(() -> {
//            switchToGameScreen();
//        });
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

    public GameClient getGameClient() {
        return gameClient;
    }


     public Refrigerator getRefrigerator() {
        return refrigerator;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}

