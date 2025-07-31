package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.proj.Model.GameAssetManager;
import com.proj.Model.TimeAndWeather.time.*;
import com.proj.Model.TimeAndWeather.TimeRenderer;
import com.proj.Model.TimeAndWeather.WeatherController;
import com.proj.Player;
import com.proj.map.FarmInOutPoint;
import com.proj.map.GameMap;
import com.proj.map.Season;

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
    private String farmName;

    private final WeatherController weatherController;
    private TimeRenderer timeRenderer;
    private Time gameTime;
    private Stage uistage;

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


    public WorldController(String landName, Time gameTime, Stage uisatge) {
        this.gameTime = gameTime;
        this.nightRender = new NightRender();
        this.farmName = landName;
        currentSeason = gameTime.getSeason();
        instance = this;
        loadMaps();
        gameMap = gameMaps.get(1);
        this.uistage = uisatge;
        this.weatherController = new WeatherController();
        clockWidget = new ClockWidget(gameTime);
        timeDisplayActor = new TimeDisplayActor(gameTime);
        dateDisplayActor = new DateDisplayActor(gameTime);
        uistage.addActor(clockWidget);
        uistage.addActor(timeDisplayActor);
        uistage.addActor(dateDisplayActor);
        positionUiElement(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        farmInOutPoints = GameAssetManager.getGameAssetManager().getExitPointList();
        currentFarmInOutPoint = findExitEnterPointsById(maps.get(landName));
        foragingManager = new ForagingManager();
    }

    private void loadMaps() {
        maps.put(farmName, 1);
        maps.put("BusStop", 2);
        maps.put("Town", 3);
        maps.put("Mountain", 4);
        maps.put("BackWoods", 5);
        maps.put("Forest", 6);
        maps.put("Beach", 7);

        for (String string : maps.keySet()) {
            gameMaps.put(maps.get(string), new GameMap(string, currentSeason));
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
                if(mapId == 2 || mapId == 3) continue;
                foragingManager.setCurrentMap(gameMaps.get(mapId));
                foragingManager.spawnDailyItems(gameTime.getSeason());
            }
            foragingManager.setCurrentMap(gameMap);
        }
        nightRender.update(gameTime);
        gameMap.setNightMode(gameTime.isNight());
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
        }
        return gameMap.isPassable(x, y);
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
        foragingManager.setCurrentMap(gameMap);

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
}
