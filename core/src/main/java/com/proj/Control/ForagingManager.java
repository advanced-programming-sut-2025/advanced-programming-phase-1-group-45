package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.inventoryItems.ForagingItem;
import com.proj.Model.GameAssetManager;
import com.proj.map.GameMap;
import com.proj.map.Season;
import com.proj.map.Tile;
import com.proj.map.TileType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ForagingManager {
    private Array<ForagingItem> foragingCrops = new Array<>();
    private GameMap gameMap;
    private final Random random = new Random();
    private Array<Tile> emptyTiles = new Array<>();
    private int emptyTilesCount = 0;
    private Array<ForagingItem> foragingMinerals = new Array<>();

    public ForagingManager() {
        foragingCrops = GameAssetManager.getGameAssetManager().getForagingCrops();
        foragingMinerals = GameAssetManager.getGameAssetManager().getForagingMinerals();
    }

    public void setGameMap(GameMap map) {
        this.gameMap = map;
        findEmptyTiles();
    }

    public void findEmptyTiles() {
        emptyTiles.clear();
        emptyTilesCount = 0;
        for (Tile[] tile : gameMap.getLandLoader().getTiles()) {
            for (Tile tile1 : tile) {
                if (tile1.isPassable() && !tile1.isEnterPoint()) {
                    if (!emptyTiles.contains(tile1, true)) {
                        emptyTiles.add(tile1);
                        emptyTilesCount++;
                    }
                }
            }
        }
    }

    public void spawnDailyRandomItems(Season currentSeason) {
        findEmptyTiles();
        gameMap.removeForaging();
        if (gameMap.getMapName().equalsIgnoreCase("cave")) {
            spawnForagingMineral();
        } else {
            spawnDailyItems(currentSeason);
        }
    }

    public void spawnDailyItems(Season currentSeason) {
        gameMap.removeForaging();
        Array<ForagingItem> seasonalItems = new Array<>();
        for (ForagingItem item : foragingCrops) {
            if (item.getSeason().contains(currentSeason)) {
                seasonalItems.add(item);
            }
        }
        if (seasonalItems.size == 0) return;
        int totalTiles = gameMap.getMapWidth() * gameMap.getMapHeight();
        int itemsToSpawn = Math.max(1, (int) (emptyTilesCount * 0.005));
        for (int i = 0; i < Math.min(itemsToSpawn, emptyTiles.size) ; i++) {
            Tile tile = emptyTiles.random();
            emptyTiles.removeValue(tile, true);
            Point position = tile.getLocation();
            //findValidSpawnPosition();
            ForagingItem template = seasonalItems.random();

            ForagingItem newItem = new ForagingItem(
                template.getName(),
                template.getSeason().toArray(new Season[0]),
                template.getBaseSellPrice(),
                template.getEnergy(),
                template.getTexture()
            );

            newItem.setPosition(position);
            gameMap.putForagingInTile(position.x, position.y, newItem);
        }

        Tile[][] tiles = gameMap.getLandLoader().getTiles();
        TiledMapTileLayer resourceLayer = (TiledMapTileLayer) gameMap.getLandLoader().getMap().getLayers().get("Paths");

        if (resourceLayer == null) {
            Gdx.app.error("ForagingManager", "ResourcesLayer not found!");
            return;
        }

        TiledMapTileSet pathTileSet = gameMap.getLandLoader().getMap().getTileSets().getTileSet("Paths");
        if (pathTileSet == null) {
            Gdx.app.error("ForagingManager", "Paths tileset not found!");
            return;
        }

        int firstGid = pathTileSet.getProperties().get("firstgid", Integer.class);

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                if (tiles[x][y] == null) continue;

                TileType type = tiles[x][y].getType();
                if (type == null) continue;

                TiledMapTile tile = null;
                switch (type) {
                    case FIBER:
                        tiles[x][y].setObject(GameAssetManager.getGameAssetManager().getNaturalResourceList().get("fiber"));
                        tile = pathTileSet.getTile(firstGid + 13);
                        break;
                    case STONE:
                        tile = pathTileSet.getTile(firstGid + 16);
                        break;
                    case WOOD:
                        tile = pathTileSet.getTile(firstGid + 18);
                        break;
                }

                if (tile != null) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(tile);
                    resourceLayer.setCell(x, y, cell);
                    tiles[x][y].setPassable(false);
                }
            }
        }
    }

    private void spawnForagingMineral() {
        gameMap.removeForaging();
        Array<ForagingItem> foragingItems = foragingMinerals;
        int itemsToSpawn = Math.max(1, (int) (emptyTilesCount * 0.0007));
        for (int i = 0; i < itemsToSpawn; i++) {
            Tile tile = emptyTiles.random();
            if (tile.getLocation().x == 16 && tile.getLocation().y == 2) {
                tile = emptyTiles.get(2);
            }
            emptyTiles.removeValue(tile, true);
            Point position = tile.getLocation();
            ForagingItem template = foragingItems.random();

            ForagingItem newItem = new ForagingItem(
                template.getName(),
                template.getSeason().toArray(new Season[0]),
                template.getBaseSellPrice(),
                template.getEnergy(),
                template.getTexture()
            );

            newItem.setPosition(position);
            gameMap.putForagingMineralInTile(position.x, position.y, newItem);
        }
    }

    private Point findValidSpawnPosition() {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(emptyTiles.toArray().length);
            if (gameMap.isPassable(emptyTiles.get(x).getLocation().x,
                emptyTiles.get(x).getLocation().y)) {
                return emptyTiles.get(x).getLocation();
            }
            attempts++;
        }
        return new Point(0, 0);
    }

    public ForagingItem tryCollectItem(Point playerTilePosition, String toolName) {
        return gameMap.harvestForagingItem(playerTilePosition, toolName);
    }

}
