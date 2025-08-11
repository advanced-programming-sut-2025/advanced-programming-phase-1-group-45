package com.proj.Control;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Model.CropInfoWindow;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.TimeAndWeather.time.*;
import com.proj.Model.TimeAndWeather.TimeRenderer;
import com.proj.Model.TimeAndWeather.WeatherController;
import com.proj.Model.inventoryItems.ForagingInventoryWindow;
import com.proj.Model.inventoryItems.SeedInventoryWindow;
import com.proj.Model.inventoryItems.crops.CropInventoryWindow;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;
import com.proj.Player;
import com.proj.map.FarmInOutPoint;
import com.proj.map.GameMap;
import com.proj.map.Season;
import com.proj.GameScreen;
import com.proj.map.farmName;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldController {
    private static WorldController instance;

    public static WorldController getInstance() {
        return instance;
    }

    private GameMap gameMap;
    //private String farmName;
    private String currentFarmName;

    private final WeatherController weatherController;
    private TimeRenderer timeRenderer;
    private Time gameTime;
    private Stage uistage;

    private SeedInventoryWindow seedWindow;
    private TextButton showSeedsButton;
    private ForagingInventoryWindow foragingInventoryWindow;
    private TextButton showForagingButton;
    private CropInventoryWindow cropInventoryWindow;
    private TextButton showCropButton;
    private CropInfoWindow cropInfoWindow;
    private TextButton showCropInfoButton;

    private ClockWidget clockWidget;
    private TimeDisplayActor timeDisplayActor;
    private DateDisplayActor dateDisplayActor;
    private NightRender nightRender;

    private Season currentSeason;

    private HashMap<String, Integer> maps = new HashMap<>();
    private HashMap<Integer, GameMap> gameMaps = new HashMap<>();
    private List<FarmInOutPoint> farmInOutPoints = new ArrayList<>();
    private FarmInOutPoint currentFarmInOutPoint;

    private Player player;

    private ForagingManager foragingManager;
    private NPCManager npcManager;
    private boolean initialized = false;
    private GameMap currentMap;
    private String currentMapName;

    private Point cavePoint;
    private GameMap caveMap;


    public WorldController(String landName, Time gameTime, Stage uisatge) {
        this.gameTime = gameTime;
        this.nightRender = new NightRender();
        this.currentFarmName = landName;
        currentSeason = gameTime.getSeason();
        this.currentMapName = landName;
        this.currentSeason = gameTime.getSeason();
        instance = this;
        this.uistage = uistage;
        loadMaps();
        gameMap = gameMaps.get(1);
        this.uistage = uisatge;
        this.weatherController = new WeatherController();
        this.npcManager = new NPCManager();  // Initialize NPC manager
        if (maps.containsKey(landName)) {
            currentMap = gameMaps.get(maps.get(landName));
        } else {
            throw new RuntimeException("Map not found: " + landName);
        }
        initUI();
        clockWidget = new ClockWidget(gameTime);
        timeDisplayActor = new TimeDisplayActor(gameTime);
        dateDisplayActor = new DateDisplayActor(gameTime);
        uistage.addActor(clockWidget);
        uistage.addActor(timeDisplayActor);
        uistage.addActor(dateDisplayActor);

        InventoryManager.getInstance().getPlayerInventory().addItem(ItemRegistry.getInstance().get("tulip_bulb"));

        Skin stardewSkin = GameAssetManager.getGameAssetManager().getStardewSkin();
        seedWindow = new SeedInventoryWindow(stardewSkin, gameMaps.get(1).getFarmingController());
        seedWindow.setVisible(false);

        foragingInventoryWindow = new ForagingInventoryWindow(stardewSkin, gameMaps.get(1).getFarmingController());
        foragingInventoryWindow.setVisible(false);

        cropInventoryWindow = new CropInventoryWindow(stardewSkin);
        cropInventoryWindow.setVisible(false);

        cropInfoWindow = new CropInfoWindow(stardewSkin);
        cropInfoWindow.setVisible(false);

        uistage.addActor(seedWindow);
        uistage.addActor(foragingInventoryWindow);
        uistage.addActor(cropInventoryWindow);
        uistage.addActor(cropInfoWindow);

        showSeedsButton = new TextButton("Seed", stardewSkin);
        showSeedsButton.setSize(157, 80);
        showSeedsButton.setPosition(
            Gdx.graphics.getWidth() - showSeedsButton.getWidth() - 1,
            Gdx.graphics.getHeight() - showSeedsButton.getHeight() - 20
        );
        showSeedsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seedWindow.setVisible(!seedWindow.isVisible());
            }
        });
        showSeedsButton.toFront();
        uistage.addActor(showSeedsButton);

        showForagingButton = new TextButton("Foraging", stardewSkin);
        showForagingButton.setSize(157, 80);
        showForagingButton.setPosition(
            Gdx.graphics.getWidth() - showForagingButton.getWidth() - 1,
            Gdx.graphics.getHeight() - showSeedsButton.getHeight() - 180
        );
        showForagingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                foragingInventoryWindow.setVisible(!foragingInventoryWindow.isVisible());
            }
        });
        showForagingButton.toFront();
        uistage.addActor(showForagingButton);

        showCropButton = new TextButton("Crop", stardewSkin);
        showCropButton.setSize(157, 80);
        showCropButton.setPosition(
            Gdx.graphics.getWidth() - showCropButton.getWidth() - 1,
            Gdx.graphics.getHeight() - showCropButton.getHeight() - 300
        );
        showCropButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cropInventoryWindow.setVisible(!cropInventoryWindow.isVisible());
            }
        });
        showCropButton.toFront();
        uistage.addActor(showCropButton);


        showCropInfoButton = new TextButton("Crop_info", stardewSkin);
        showCropInfoButton.setSize(157, 80);
        showCropInfoButton.setPosition(
            Gdx.graphics.getWidth() - showCropInfoButton.getWidth() - 1,
            Gdx.graphics.getHeight() - showCropInfoButton.getHeight() - 400
        );
        showCropInfoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cropInfoWindow.setVisible(!cropInfoWindow.isVisible());
            }
        });
        showCropInfoButton.toFront();
        uistage.addActor(showCropInfoButton);


        positionUiElement(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        farmInOutPoints = GameAssetManager.getGameAssetManager().getExitPointList(gameMap.getMapName());
        currentFarmInOutPoint = findExitEnterPointsById(maps.get(landName));
        foragingManager = new ForagingManager();
    }

    private void initUI() {
        if (uistage == null) {
            throw new IllegalStateException("Stage must be initialized first");
        }

        clockWidget = new ClockWidget(gameTime);
        timeDisplayActor = new TimeDisplayActor(gameTime);
        dateDisplayActor = new DateDisplayActor(gameTime);

        uistage.addActor(clockWidget);
        uistage.addActor(timeDisplayActor);
        uistage.addActor(dateDisplayActor);

        positionUiElement(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    private void loadMaps() {
        maps.put(currentMapName, 1);
        maps.put("BusStop", 2);
        maps.put("Town", 3);
        maps.put("Mountain", 4);
        maps.put("BackWoods", 5);
        maps.put("Forest", 6);
        maps.put("Beach", 7);

        for (String string : maps.keySet()) {
            FarmingController farmingController = null;
            if (maps.get(string) == 1) {
                farmingController = new FarmingController();
            }
            gameMaps.put(maps.get(string), new GameMap(string, currentSeason, farmingController));
        }

        if (!currentMapName.equalsIgnoreCase("Island")) {
            caveMap = new GameMap("cave", currentSeason, null);
            for (farmName fa : farmName.values()) {
                if (fa.getFarmName().equals(currentMapName)) {
                    cavePoint = GameAssetManager.getGameAssetManager().getCavePoint(fa);
                    break;
                }
            }
        }

    }

    private FarmInOutPoint findExitEnterPointsById(int mapId) {
        for (FarmInOutPoint farmInOutPoint : farmInOutPoints) {
            if (farmInOutPoint.getMapId() == mapId) {
                return farmInOutPoint;
            }
        }
        return null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void update(float delta) {
        Season newSeason = gameTime.getSeason();
        if (currentSeason != newSeason) {
            currentSeason = newSeason;
            for (Integer i : gameMaps.keySet()) {
                gameMaps.get(i).changeSeason(newSeason);
            }
        }
        weatherController.update(gameTime.getWeather(), delta);
        if (gameTime.isNewDay()) {
            for (Integer mapId : gameMaps.keySet()) {
                if (mapId == 2 || mapId == 3) {
                    continue;
                }
                foragingManager.setGameMap(gameMaps.get(mapId));
                foragingManager.spawnDailyRandomItems(gameTime.getSeason());
                gameMaps.get(mapId).updateDaily(gameTime.getSeason());
            }
            if(cavePoint != null) {
                foragingManager.setGameMap(caveMap);
                foragingManager.spawnDailyRandomItems(gameTime.getSeason());
            }
            foragingManager.setGameMap(gameMap);
        }
        if (gameMap.getFarmingController() != null) {
            gameMap.getFarmingController().getCropManager().update(delta);
        }
        nightRender.update(gameTime);
        gameMap.setNightMode(gameTime.getHour() >= 19);
    }

    public void render(OrthographicCamera camera) {
        gameMap.render(camera);
        gameMap.getSpriteBatch().setProjectionMatrix(camera.combined);
        gameMap.getSpriteBatch().begin();

        weatherController.render(gameMap.getSpriteBatch(), gameTime.getWeather());
    }

    public void renderAfterPlayer() {
        gameMap.renderLandObject();
        nightRender.render(gameMap.getSpriteBatch());
        gameMap.renderLights();
    }

    public void resize(int width, int height) {
        uistage.getViewport().update(width, height, true);
        positionUiElement(width, height);

        showSeedsButton.toFront();
        positionUiElement(width, height);
        showSeedsButton.setPosition(
            showSeedsButton.getWidth() - 14,
            showSeedsButton.getHeight() + 20
        );
        seedWindow.centerWindow();

        showForagingButton.toFront();
        positionUiElement(width, height);
        showForagingButton.setPosition(
            showForagingButton.getWidth() - 14,
            showForagingButton.getHeight() + 120
        );
        foragingInventoryWindow.centerWindow();

        showCropButton.toFront();
        positionUiElement(width, height);
        showCropButton.setPosition(
            showCropButton.getWidth() - 14,
            showCropButton.getHeight() + 220
        );
        cropInventoryWindow.centerWindow();


        showCropInfoButton.toFront();
        positionUiElement(width, height);
        showCropInfoButton.setPosition(
            showCropInfoButton.getWidth() - 14,
            showCropInfoButton.getHeight() + 320
        );
        cropInfoWindow.centerWindow();


        if (seedWindow != null) {
            seedWindow.setPosition(
                (width - seedWindow.getWidth()) / 2,
                (height - seedWindow.getHeight()) / 2
            );
        }

        weatherController.resize((int) width, (int) height);
        nightRender.resize(width, height);
    }

    public void dispose() {
        gameMap.dispose();
        weatherController.dispose();
        nightRender.dispose();
    }

    public void positionUiElement(float width, float height) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        clockWidget.setPosition(
            screenWidth - clockWidget.getWidth() - 20,
            screenHeight - clockWidget.getHeight() - 20
        );

        timeDisplayActor.setPosition(
            clockWidget.getX() + 130,
            clockWidget.getY() + 92
        );

        dateDisplayActor.setPosition(
            clockWidget.getX() + 128,
            clockWidget.getY() + 155
        );
    }


    public int getMapWidth() {
        return gameMap.getMapWidth();
    }

    public int getMapHeight() {
        return gameMap.getMapHeight();
    }

    public int getTileWidth() {
        return gameMap.getTileWidth();
    }

    public int getTileHeight() {
        return gameMap.getTileHeight();
    }

    public Point getPlayerSpawnPoint() {
        return gameMap.getPlayerSpawnPoint();
    }

    public boolean isExitPoint(Point targetPoint) {
        return currentFarmInOutPoint.farmExitPoints.containsKey(targetPoint);
    }

    public boolean isPassable(float x, float y) {
        int tileX = (int) (x / gameMap.getTileWidth());
        int tileY = (int) (y / gameMap.getTileHeight());
        Point point = new Point(tileX, tileY);
        if (isExitPoint(point)) {
            triggerToOtherMap(point);
            return false;
        } else if (gameMap.getMapName().equalsIgnoreCase("cave")) {
            if ((tileX == 16 && tileY == 1)|| (tileX == 15 && tileY == 1) ) {
                backToFarm();
            }
        } else if (cavePoint.x == point.x && cavePoint.y == point.y) {
            triggerToCave();
            return false;
        }
        return gameMap.isPassable(x, y);
    }

    public void triggerToCave() {
        gameMap = caveMap;
        foragingManager.setGameMap(caveMap);
        player.setPosition(16 * 16, 2 * 16);
        player.setTargetPosition(16 * 16, 2 * 16);
    }

    public void backToFarm() {
        gameMap = gameMaps.get(1);
        foragingManager.setGameMap(gameMap);
        player.setPosition(cavePoint.x * gameMap.getTileWidth(), (cavePoint.y - 1) * gameMap.getTileHeight());
        player.setTargetPosition(cavePoint.x * gameMap.getTileWidth(), (cavePoint.y - 1) * gameMap.getTileHeight());
    }

    private void triggerToOtherMap(Point point) {
        int newMapId = currentFarmInOutPoint.farmExitPoints.get(point);
        String newMapName = null;
        for (String name : maps.keySet()) {
            if (maps.get(name).equals(newMapId)) {
                newMapName = name;
                break;
            }
        }
        gameMap = gameMaps.get(newMapId);
        foragingManager.setGameMap(gameMap);

        FarmInOutPoint nexMap = findExitEnterPointsById(newMapId);
        if (nexMap == null) {
            return;
        }
        Point targetPoint = null;
        for (Point pointy : nexMap.farmEnterPoints.keySet()) {
            if (nexMap.farmEnterPoints.get(pointy).equals(currentFarmInOutPoint.getMapId())) {
                targetPoint = pointy;
                break;
            }
        }
        if (targetPoint != null) {
            player.setPosition(targetPoint.x * gameMap.getTileWidth() + (float) gameMap.getTileWidth() / 2,
                targetPoint.y * gameMap.getTileHeight() + (float) gameMap.getTileHeight() / 2);
            player.setTargetPosition(targetPoint.x * gameMap.getTileWidth() + (float) gameMap.getTileWidth() / 2,
                targetPoint.y * gameMap.getTileHeight() + (float) gameMap.getTileHeight() / 2);
        }
        currentFarmInOutPoint = nexMap;
    }

    public SpriteBatch getSpriteBatch() {
        return gameMap.getSpriteBatch();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Player getPlayer() {
        return player;
    }

    public ForagingManager getForagingManager() {
        return foragingManager;
    }

    public void renderMap(OrthographicCamera camera) {
        if (gameMap != null) {
            gameMap.render(camera);
        }
    }

    /*public void triggerToNewScreen(String newMapName, Point spawnPoint) {
        GameScreen newScreen = new GameScreen(getFarmNameFromString(newMapName));
        newScreen.setPlayerSpawnPoint(spawnPoint);
        newScreen.setNPCManager(this.npcManager);
        if (this.player != null) {
            newScreen.setPlayer(this.player);
        }
        ((Game) Gdx.app.getApplicationListener()).setScreen(newScreen);
    }*/

    private farmName getFarmNameFromString(String name) {
        for (farmName farm : farmName.values()) {
            if (farm.getFarmName().equalsIgnoreCase(name)) {
                return farm;
            }
        }
        return farmName.STANDARD;
    }

}
