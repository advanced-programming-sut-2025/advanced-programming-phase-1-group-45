
package com.proj.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.proj.Control.FarmingController;
import com.proj.Model.GameAssetManager;
import com.proj.Model.TimeAndWeather.time.LanternLightSystem;
import com.proj.Model.inventoryItems.ForagingItem;
import com.proj.Model.inventoryItems.ResourceItem;
import com.proj.Model.inventoryItems.SeedItem;
import com.proj.Model.inventoryItems.crops.CropManager;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;
import com.proj.Model.inventoryItems.trees.TreeManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private LandLoader loader;
    TiledMap tiledMap;
    private String mapName;
    private Point playerSpawnPoint;
    private LanternLightSystem lanternLightSystem;
    private List<Tile> foragingCropTiles;
    private FarmingController farmingController;
    private Season currentSeason;
    private TiledMapTile tilledDirt;

    public GameMap(String farmName, Season season, FarmingController farmingController) {
        mapName = farmName;
        batch = new SpriteBatch();
        loader = new LandLoader(farmName, season);
        this.currentSeason = season;
        tiledMap = loader.getMap();
        playerSpawnPoint = loader.getPlayerSpawnPoint();
        mapRenderer = new OrthogonalTiledMapRenderer(loader.getMap());
        initializeLanternSystem();
        foragingCropTiles = new ArrayList<>();
        this.farmingController = farmingController;
        if (farmingController != null) {
            farmingController.setMap(this);
            findTilledDirt();
            farmingController.plantSeed((SeedItem) ItemRegistry.getInstance().get("tulip_bulb"), 45,45);
        }
    }

    public void findTilledDirt() {
        try {
            TiledMapTileSet tileSet = loader.getMap().getTileSets().getTileSet("untitled tile sheet");
            int firstGId = tileSet.getProperties().get("firstgid", Integer.class);
            tilledDirt = tileSet.getTile(firstGId + 681);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public LandLoader getLandLoader() {
        return loader;
    }

    public TreeManager getTreeManager() {
        return farmingController.getTreeManager();
    }
    public CropManager getCropManager() {
        return farmingController.getCropManager();
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void changeSeason(Season season) {
        loader.changeSeason(season);
        tiledMap = loader.getMap();
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        initializeLanternSystem();
    }

    public void updateDaily(Season season) {
        this.currentSeason = season;
        if (farmingController == null) return;
        farmingController.updateDaily(currentSeason);
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void renderLandObject() {
        for (Tile tile : foragingCropTiles) {
            ForagingItem item = (ForagingItem) tile.getLandObject();
            Point pos = item.getPosition();
            batch.draw(item.getTexture(),
                pos.x * loader.getTileWidth(),
                pos.y * loader.getTileHeight());
        }
        if (farmingController != null) {
            farmingController.renderAll(batch, currentSeason);
        }
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

    public boolean canPlantInTile(int x, int y) {
        Tile tile = loader.getTiles()[x][y];
        if (tile == null) return false;
        if (!tile.isPassable()) return false;
        if (!tile.isTilled()) return false;
        return true;
    }

    public void putForagingInTile(int x, int y, LandObject foragingObject) {
        Tile tile = loader.getTiles()[x][y];
        tile.setObject(foragingObject);
        tile.setPassable(false);
        tile.setType(TileType.FORAGING_CROP);
        foragingCropTiles.add(tile);
    }

    public void removeForaging() {
        for (Tile tile : foragingCropTiles) {
            tile.setPassable(true);
            tile.removeObject();
        }
        foragingCropTiles.clear();
    }

    public ForagingItem harvestForagingItem(Point playerPoint, String toolName) {
        Tile tile = loader.getTiles()[playerPoint.x][playerPoint.y];
        if (tile == null) return null;
        Object item = tile.getLandObject();
        if (item instanceof ForagingItem) {
            ForagingItem itemForagingItem = (ForagingItem) item;
            if (toolName.equals("Scythe")) {
                tile.removeObject();
                tile.setPassable(true);
                tile.setType(null);
                foragingCropTiles.remove(tile);
                return itemForagingItem;
            }
        }
        return null;
    }

    public boolean hoeTile(int x, int y) {
        Tile tile = loader.getTiles()[x][y];
        if (tile == null) return false;
        if (tile.isOccupied() || tile.getLandObject() != null) return false;
        tile.setTilled(true);
        tile.removeObject();
        TiledMapTileLayer resourceLayer = (TiledMapTileLayer) loader.getMap().getLayers().get("Back");
        if (resourceLayer != null) {
            TiledMapTileLayer.Cell cell = resourceLayer.getCell(x, y);
            if (cell == null) return false;
            cell.setTile(tilledDirt);
            resourceLayer.setCell(x, y, cell);
        }
        restoreOriginalTile(x, y);
        if (resourceLayer == null) return false;
        return true;
    }


//    public ResourceItem pickNaturalResource(Point playerPoint) {
//        Tile tile = loader.getTiles()[playerPoint.x][playerPoint.y];
//        if (tile == null) return null;
//        Object item = tile.getLandObject();
//        if (item instanceof ResourceItem) {
//            ResourceItem naturalResource = (ResourceItem) item;
//            tile.removeObject();
//            tile.setPassable(true);
//            for (MapLayer layer : loader.getMap().getLayers()) {
//                if (layer instanceof TiledMapTileLayer) {
//                    TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(playerPoint.x, playerPoint.y);
//                    if (cell == null) continue;
//                    int first = loader.getMap().getTileSets().getTileSet("untitled tile sheet").
//                        getProperties().get("firstgid", Integer.class);
//                    TiledMapTile tile1 = loader.getMap().getTileSets().getTileSet("untitled tile sheet").
//                        getTile(first + 227);
//                    if (tile1 == null) continue;
//                    cell.setTile(tile1);
//                }
//            }
//            return naturalResource;
//        }
//        return null;
//    }


    public ResourceItem pickNaturalResource(Point playerPoint) {
        Tile tile = loader.getTiles()[playerPoint.x][playerPoint.y];
        if (tile == null) return null;
        Object item = tile.getLandObject();
        if (item instanceof ResourceItem) {
            ResourceItem naturalResource = (ResourceItem) item;
            tile.removeObject();
            tile.setPassable(true);
            TiledMapTileLayer resourceLayer = (TiledMapTileLayer) loader.getMap().getLayers().get("Paths");
            if (resourceLayer != null) {
                resourceLayer.setCell(playerPoint.x, playerPoint.y, null);
            }
            restoreOriginalTile(playerPoint.x, playerPoint.y);
            return naturalResource;
        }
        return null;
    }

    private void restoreOriginalTile(int x, int y) {
        TiledMapTileLayer backLayer = (TiledMapTileLayer) loader.getMap().getLayers().get("Back");
        if (backLayer == null) return;
        TiledMapTileLayer.Cell cell = loader.getOriginalGroundCells().get(new Point(x, y));
        if (cell != null) {
            backLayer.setCell(x, y, cell);
        }
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

    public String getMapName() {
        return mapName;
    }

    public Tile getTile(int x, int y) {
        return loader.getTiles()[x][y];
    }

    public FarmingController getFarmingController() {
        return farmingController;
    }

}
