
package com.proj.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.proj.Model.GameAssetManager;
import com.proj.Model.TimeAndWeather.time.LanternLightSystem;

import java.awt.*;

public class GameMap {
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private LandLoader loader;
    TiledMap tiledMap;
    private String mapName;
    private Point playerSpawnPoint;
    private LanternLightSystem lanternLightSystem;


    public GameMap(String farmName, Season season) {
        mapName = farmName;
        batch = new SpriteBatch();
        loader = new LandLoader(farmName, season);
        tiledMap = loader.getMap();
        playerSpawnPoint = loader.getPlayerSpawnPoint();
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap());
        initializeLanternSystem();
    }

    public LandLoader getLandLoader() {
        return loader;
    }

    public void changeSeason(Season season) {
        loader.changeSeason(season);
        tiledMap = loader.getMap();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        initializeLanternSystem();
    }

    public void changeMap(String mapName, Season season) {
        loader = new LandLoader(mapName, season);
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap());
        initializeLanternSystem();
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    public boolean isPassable(float x, float y) {

        return loader.isPassable(x, y);
    }

    private void initializeLanternSystem() {
        TextureRegion light = new TextureRegion(GameAssetManager.getGameAssetManager().getLanternLight());
        lanternLightSystem = new LanternLightSystem(loader.getMap(), light, loader.getTileWidth(), loader.getTileHeight());
    }

    public void setNightMode(boolean isNight) {
        lanternLightSystem.setNightMode(isNight);
    }

    public void renderLights() {
        lanternLightSystem.render(batch);
    }


    public void dispose() {
        tiledMap.dispose();
        mapRenderer.dispose();
        batch.dispose();
    }

    public int getMapWidth() {
        return loader.getMapWidth();
    }

    public int getMapHeight() {
        return loader.getMapHeight();
    }

    public int getTileWidth() {
        return loader.getTileWidth();
    }

    public int getTileHeight() {
        return loader.getTileHeight();
    }

    public Point getPlayerSpawnPoint() {
        return playerSpawnPoint;
    }
}
