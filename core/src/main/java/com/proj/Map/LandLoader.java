package com.proj.Map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.proj.Model.GameAssetManager;

import java.awt.*;
import java.util.List;
import java.util.*;

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
    private Map<String, Boolean> layerConfig = new HashMap<>();
    private Point playerSpawnPoint;

    int countUnPassed = 0;

    public LandLoader(String farmName, Season season) {
        this.landName = farmName;
        this.landSeason = season;
        loadMap();
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

        saveOriginalTiles();

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
                    TileType tileType = findType(cell);
                    if (tileType != null) {
                        tiles[x][y].setType(tileType);
                        if (tileType == TileType.FIBER || tileType == TileType.STONE || tileType == TileType.WOOD) {
                            tiles[x][y].setObject(GameAssetManager.getGameAssetManager().
                                getNaturalResourceList().get(tileType.toString().toLowerCase()));
                        }
                    }
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
        layerConfig.put("forageItem", false);
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

    public Tile[][] getTiles() {
        return tiles;
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

    private TileType findType(TiledMapTileLayer.Cell cell) {
        TiledMapTile tile = cell.getTile();
        TiledMapTileSet source = null;
        if (tile != null) {
            int tileId = tile.getId();
            for (TiledMapTileSet ts : map.getTileSets()) {
                if (ts.getTile(tile.getId()) == tile) {
                    source = ts;
                    break;
                }
            }
            if (source != null && source.getName().equals("Paths")) {
                int first = source.getProperties().get("firstgid", Integer.class);
                if (tileId == first + 13 || tileId == first + 14 ||
                    tileId == first + 15 || tileId == first + 23) {
                    return TileType.FIBER;
                } else if (tileId == first + 16 || tileId == first + 17) {
                    return TileType.STONE;
                } else if (tileId == first + 18) {
                    return TileType.WOOD;
                }
            }
        }
        return null;
    }

    public TiledMap getMap() {
        return map;
    }

    public Point getPlayerSpawnPoint() {
        return playerSpawnPoint;
    }

    private TiledMapTileLayer BackLayer;
    private Map<Point, TiledMapTileLayer.Cell> backCells = new HashMap<>();

    private void saveOriginalTiles() {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("Back");
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                TiledMapTileLayer.Cell cell = groundLayer.getCell(x, y);
                if (cell != null) {
                    backCells.put(new Point(x, y), cell);
                }
            }
        }
    }

    public Map<Point, TiledMapTileLayer.Cell> getOriginalGroundCells() {
        return backCells;
    }
}
