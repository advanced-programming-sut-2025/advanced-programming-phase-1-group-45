
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
    private TileProperties[][] tilesProperties;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int mapWidth = 80, mapHeight = 65;


    public GameMap() {
        MapLoader loader = MapLoader.getMapLoader();
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap());
        batch = new SpriteBatch();
        initialize();
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public SpriteBatch getSpriteBatch() {
        return batch;
    }

    private void initialize() {
        TiledMap map = MapLoader.getMapLoader().getMap();
        tilesProperties = new TileProperties[80][65];
        int passableCount = 0;
        int totalTiles = 80 * 65;
        Map<String, Boolean> layerConfig = new HashMap<>();
        layerConfig.put("Back", true);
        layerConfig.put("Path", false);
        layerConfig.put("Buildings", false);
        layerConfig.put("Front", false);
        layerConfig.put("AlwaysFront", false);
        layerConfig.put("AlwaysFront2", false);

        List<LayerInfo> collisionLayers = new ArrayList<>();
        for (String name : layerConfig.keySet()) {
            MapLayer layer = map.getLayers().get(name);
            if (layer instanceof TiledMapTileLayer) {
                collisionLayers.add(new LayerInfo(
                    (TiledMapTileLayer) layer,
                    layerConfig.get(name)
                ));
            }
        }
        for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 65; y++) {
                boolean passable = true;
                for (LayerInfo layer : collisionLayers) {
                    TiledMapTileLayer.Cell cell = layer.tileLayer.getCell(x, y);
                    if (cell == null || cell.getTile() == null) continue;

                    Boolean walkable = getWalkableProperty(cell);
                    if (walkable == null) walkable = layer.defaultWalkable;

                    if (!walkable) {
                        passable = false;
                        break;
                    }
                }
                tilesProperties[x][y] = new TileProperties(
                    passable ? TileProperties.Passable : 0
                );

                if (passable) passableCount++;
            }
        }

        Gdx.app.log("TileProperties", "Passable tiles: " + passableCount + "/" + totalTiles);
    }

    private static class LayerInfo {
        TiledMapTileLayer tileLayer;
        boolean defaultWalkable;

        LayerInfo(TiledMapTileLayer tileLayer, boolean defaultWalkable) {
            this.tileLayer = tileLayer;
            this.defaultWalkable = defaultWalkable;
        }
    }

    private Boolean getWalkableProperty(TiledMapTileLayer.Cell cell) {
        Object prop = cell.getTile().getProperties().get("Passable");
        if (prop instanceof Boolean) return (Boolean) prop;
        if (prop instanceof String) return Boolean.parseBoolean((String) prop);
        return null;
    }

    public boolean isPassable(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return false;
        }
        return tilesProperties[x][y].isPassable();
    }

    public void dispose() {
        mapRenderer.dispose();
        batch.dispose();
    }


}
