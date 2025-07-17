package com.proj.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapLoader {
    private static MapLoader mapLoader = new MapLoader();
    public static MapLoader getMapLoader() {
        return mapLoader;
    }
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer pathLayer;
    private TiledMapTileLayer backLayer;
    private TiledMapTileLayer frontLayer, alwaysFrontLayer, alwaysFrontLayer2, buildingLayer;
    public MapLoader() {
        map = new TmxMapLoader().load("assets/farmBg.tmx");
        pathLayer = (TiledMapTileLayer) map.getLayers().get("Path");
        backLayer = (TiledMapTileLayer) map.getLayers().get("Back");
        frontLayer = (TiledMapTileLayer) map.getLayers().get("Front");
        alwaysFrontLayer = (TiledMapTileLayer) map.getLayers().get("AlwaysFront");
        alwaysFrontLayer2 = (TiledMapTileLayer) map.getLayers().get("AlwaysFront2");
        buildingLayer = (TiledMapTileLayer) map.getLayers().get("Building");
    }

    public TiledMap getMap() {
        return map;
    }

    public TiledMapTileLayer getBuildingLayer() {
        return buildingLayer;
    }

    public TiledMapTileLayer getAlwaysFrontLayer2() {
        return alwaysFrontLayer2;
    }

    public TiledMapTileLayer getAlwaysFrontLayer() {
        return alwaysFrontLayer;
    }

    public TiledMapTileLayer getFrontLayer() {
        return frontLayer;
    }

    public TiledMapTileLayer getBackLayer() {
        return backLayer;
    }

    public TiledMapTileLayer getPathLayer() {
        return pathLayer;
    }
}
