package com.proj.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
//import jdk.internal.org.commonmark.internal.inline.BackslashInlineParser;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class LandLoader {
    private String landName;
    private String landPath;
    private Season landSeason;
    private TiledMap map;
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private Tile[][] tiles;
    private TileProperties[][] properties;
    private Map<String, Boolean> layerConfig = new HashMap<>();
    private Point playerSpawnPoint;

    int countUnPassed = 0;

    public LandLoader(String farmName, Season season) {
        this.landName = farmName;
        this.landSeason = season;
        loadMap();
//        initialize();
//        findPlayerSpawnPoint();
    }


    private void loadMap() {
        String seasonName = landSeason.toString().toLowerCase();
        this.landPath = "assets/map/land/" + seasonName + "/" + landName + "_" + seasonName + ".tmx";
        map = new TmxMapLoader().load(landPath);

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        System.err.println("width: " + mapWidth + " height: " + mapHeight);

        initialize();

        if (Arrays.stream(farmName.values()).anyMatch(name -> name.getFarmName().equals(landName))) {
            findPlayerSpawnPoint();
        }
    }

    public void changeSeason(Season newSeason) {
        this.landSeason = newSeason;
        String seasonName = landSeason.toString().toLowerCase();
        this.landPath = "assets/map/land/" + seasonName + "/" + landName + "_" + seasonName + ".tmx";
        loadMap();
    }

    private void initialize() {

        tiles = new Tile[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                tiles[x][y] = new Tile(new Point(x, y));
                tiles[x][y].setPassable(true);
            }
        }

        makeLayerConfig(layerConfig);

        List<LayerInfo> collisionLayers = new ArrayList<>();

        makeCollisionLayer(collisionLayers);


        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
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

                    if (getHomeGate(cell)) {
                        tiles[x][y].setHomeGate(true);
                    }
                }
                tiles[x][y].setPassable(passable);
                if (!passable) countUnPassed++;

            }
        }

    }

    private void findPlayerSpawnPoint() {
        for (Tile[] tile : tiles) {
            for (Tile tile1 : tile) {
                if (tile1.isHomeGate()) {
                    playerSpawnPoint = tile1.getLocation();
                    return;
                }
            }
        }
        playerSpawnPoint = null;
    }

    private void makeCollisionLayer(List<LayerInfo> collisionLayers) {
        for (String name : layerConfig.keySet()) {
            MapLayer layer = map.getLayers().get(name);
            if (layer instanceof TiledMapTileLayer) {
                collisionLayers.add(new LayerInfo(
                    (TiledMapTileLayer) layer,
                    layerConfig.get(name)
                ));
            }
        }
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
        if (prop instanceof Boolean) {
            return (Boolean) prop;
        }
        if (prop instanceof String) {
            if (prop.equals("T")) {
                return true;
            } else if (prop.equals("F")) {
                return false;
            }
        }
        return null;
    }

    private Boolean getHomeGate(TiledMapTileLayer.Cell cell) {
        Object gateWay = cell.getTile().getProperties().get("home_gate");
        if (gateWay != null) {
            return true;
        }
        return false;
    }


    static void makeLayerConfig(Map<String, Boolean> layerConfig) {
        layerConfig.put("Back", true);
        layerConfig.put("Back2", true);
        layerConfig.put("Back3", true);
        layerConfig.put("Tree", true);
        layerConfig.put("Path", false);
        layerConfig.put("Paths", false);
        layerConfig.put("Buildings", false);
        layerConfig.put("Buildings2", false);
        layerConfig.put("MainHouse", false);
        layerConfig.put("Front", false);
        layerConfig.put("AlwaysFront", false);
        layerConfig.put("AlwaysFront2", false);
        layerConfig.put("AlwaysFront3", false);
    }


    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }


    public boolean isPassable(float x, float y) {
        int tileX = (int) (x / tileWidth);
        int tileY = (int) (y / tileHeight);
        if (tileY >= mapHeight || tileX >= mapWidth || tileX <= 0 || tileY <= 0) {
            System.err.println("x : " + tileX + " y : " + tileY);

            System.err.println("out of bounds");
            return false;
        }
        Tile tile = tiles[tileX][tileY];
        if (tile == null) {
            System.err.println("out of bounds");
            return false;
        }
        return tile.isPassable();
    }

    public TiledMap getMap() {
        return map;
    }

    public Point getPlayerSpawnPoint() {
        return playerSpawnPoint;
    }
}
