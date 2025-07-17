
package com.proj.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private LandLoader loader;
    TiledMap tiledMap;

    public GameMap() {
        batch = new SpriteBatch();
        loader = new LandLoader("Town5", Season.SPRING);
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap());
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
}
