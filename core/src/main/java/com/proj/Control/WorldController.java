package com.proj.Control;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.proj.Model.CropInfoWindow;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.TimeAndWeather.time.*;
import com.proj.Model.TimeAndWeather.TimeRenderer;
import com.proj.Model.TimeAndWeather.WeatherController;
import com.proj.Model.inventoryItems.ForagingInventoryWindow;
import com.proj.Model.inventoryItems.SeedInventoryWindow;
import com.proj.Model.inventoryItems.crops.CropInventoryWindow;
import com.proj.Model.inventoryItems.fertilizer.BasicFertilizer;
import com.proj.Model.inventoryItems.fertilizer.DeluxeFertilizer;
import com.proj.Model.inventoryItems.fertilizer.FertilizeWindow;
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

    private FertilizeWindow fertilizerWindow;
    private TextButton showFertilizerButton;

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

    private ImageButton inventoryButton;
    private Table inventoryMenu; // جدول برای منوی اینونتوری
    private boolean menuVisible = false;


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
        InventoryManager.getInstance().getPlayerInventory().addItem(ItemRegistry.getInstance().get("acorn"));

        Skin stardewSkin = GameAssetManager.getGameAssetManager().getStardewSkin();
        seedWindow = new SeedInventoryWindow(stardewSkin, gameMaps.get(1).getFarmingController());
        seedWindow.setVisible(false);
        fertilizerWindow = new FertilizeWindow(stardewSkin, gameMaps.get(1).getFarmingController());
        fertilizerWindow.setVisible(false);
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
        uistage.addActor(fertilizerWindow);

        createInventoryButton();
        createInventoryMenu();


        positionUiElement(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        farmInOutPoints = GameAssetManager.getGameAssetManager().getExitPointList(gameMap.getMapName());
        currentFarmInOutPoint = findExitEnterPointsById(maps.get(landName));
        foragingManager = new ForagingManager();
    }


    private void createInventoryButton() {
        Texture inventoryTexture = GameAssetManager.getGameAssetManager().getBackpackTexture();
        TextureRegion inventoryRegion = new TextureRegion(inventoryTexture);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(inventoryRegion);

        inventoryButton = new ImageButton(style);
        inventoryButton.setSize(106, 128);

        inventoryButton.setPosition(
            Gdx.graphics.getWidth() - inventoryButton.getWidth() - 20,
            20
        );

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleInventoryMenu();
            }
        });

        uistage.addActor(inventoryButton);
    }

    private void createInventoryMenu() {
        Skin stardewSkin = GameAssetManager.getGameAssetManager().getStardewSkin();
        inventoryMenu = new Table();
        inventoryMenu.setVisible(false);
        inventoryMenu.setBackground(stardewSkin.getDrawable("background"));

        TextButton cropInfoMenuButton = createMenuButton("Crop Info", () -> {
            cropInfoWindow.setVisible(true);
            cropInfoWindow.centerWindow();
            inventoryMenu.setVisible(false);
            menuVisible = false;
        }, stardewSkin);

        TextButton cropsMenuButton = createMenuButton("Crops", () -> {
            cropInventoryWindow.setVisible(true);
            cropInventoryWindow.centerWindow();
            inventoryMenu.setVisible(false);
            menuVisible = false;
        }, stardewSkin);

        TextButton foragingMenuButton = createMenuButton("Foraging", () -> {
            foragingInventoryWindow.setVisible(true);
            foragingInventoryWindow.centerWindow();
            inventoryMenu.setVisible(false);
            menuVisible = false;
        }, stardewSkin);

        TextButton seedsMenuButton = createMenuButton("Seeds", () -> {
            seedWindow.setVisible(true);
            seedWindow.centerWindow();
            inventoryMenu.setVisible(false);
            menuVisible = false;
        }, stardewSkin);

        TextButton fertilizeMenuButton = createMenuButton("Fertilizer", () -> {
            fertilizerWindow.setVisible(true);
            fertilizerWindow.centerWindow();
            inventoryMenu.setVisible(false);
            menuVisible = false;
        }, stardewSkin);

        inventoryMenu.add(cropInfoMenuButton).pad(5).row();
        inventoryMenu.add(cropsMenuButton).pad(5).row();
        inventoryMenu.add(foragingMenuButton).pad(5).row();
        inventoryMenu.add(seedsMenuButton).pad(5).row();
        inventoryMenu.add(fertilizeMenuButton).pad(5).row();

        inventoryMenu.pack();
        uistage.addActor(inventoryMenu);
    }

    private TextButton createMenuButton(String text, Runnable action, Skin skin) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }


    private void toggleInventoryMenu() {
        menuVisible = !menuVisible;
        inventoryMenu.setVisible(menuVisible);

        if (menuVisible) {
            float x = inventoryButton.getX() + inventoryButton.getWidth() - inventoryMenu.getWidth();
            float y = inventoryButton.getY() + inventoryButton.getHeight();
            inventoryMenu.setPosition(x, y);
        }
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
            if (cavePoint != null) {
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

        inventoryButton.setPosition(
            width - inventoryButton.getWidth() - 20,
            20
        );

        if (menuVisible) {
            float x = inventoryButton.getX() + inventoryButton.getWidth() - inventoryMenu.getWidth();
            float y = inventoryButton.getY() + inventoryButton.getHeight();
            inventoryMenu.setPosition(x, y);
        }

        if (seedWindow != null) {
            seedWindow.setPosition(
                (width - seedWindow.getWidth()) / 2,
                (height - seedWindow.getHeight()) / 2
            );
        }

        if (fertilizerWindow.isVisible()) {
            fertilizerWindow.centerWindow();
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
            if ((tileX == 16 && tileY == 1) || (tileX == 15 && tileY == 1)) {
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
