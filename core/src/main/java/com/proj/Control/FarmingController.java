package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.SeedItem;
import com.proj.Model.inventoryItems.crops.Crop;
import com.proj.Model.inventoryItems.crops.CropManager;
import com.proj.Model.inventoryItems.crops.GiantCrop;
import com.proj.Model.inventoryItems.fertilizer.FertilizerSelectionListener;
import com.proj.Model.inventoryItems.seeds.SeedSelectionListener;
import com.proj.Model.inventoryItems.trees.TreeManager;
import com.proj.PlayerDirection;
import com.proj.map.GameMap;
import com.proj.map.Season;
import com.proj.map.Tile;

public class FarmingController implements SeedSelectionListener, FertilizerSelectionListener {
    private GameMap gameMap;
    private final TreeManager treeManager;
    private final CropManager cropManager;
    private Season currentSeason = Season.SPRING;
    private SeedItem selectedSeedForPlanting;
    private InventoryItem selectedFertilizer;

    public FarmingController() {
        this.treeManager = new TreeManager();
        this.cropManager = new CropManager();
    }

    public void setMap(GameMap gameMap) {
        this.gameMap = gameMap;
        treeManager.setMap(gameMap);
        cropManager.setMap(gameMap);
    }

    public void setSelectedFertilizer(InventoryItem fertilizer) {
        this.selectedFertilizer = fertilizer;
    }

    public void updateDaily(Season season) {
        currentSeason = season;
        treeManager.updateDaily(season);
        cropManager.updateDaily(season);
    }

    public void renderAll(SpriteBatch batch, Season season) {
        treeManager.renderAll(batch, season);
        cropManager.renderAll(batch, season);
    }

    public boolean plantSeed(SeedItem seed, int tileX, int tileY) {


        if (!seed.getSeasons().contains(currentSeason, false)) {
            Gdx.app.log("Farming", seed.getName() + " cannot be planted in " + currentSeason);
            return false;
        }

        if (!gameMap.canPlantInTile(tileX, tileY)) {
            Gdx.app.log("Farming", "Tile not tillable or occupied: " + tileX + "," + tileY);
            return false;
        }


        if (seed.getSeedType() == SeedItem.SeedType.TREE && seed.getPlantId() != null) {
            return treeManager.plantFromSeed(seed.getName(), tileX, tileY);
        } else if (seed.getSeedType() == SeedItem.SeedType.CROP && seed.getPlantId() != null) {
            return cropManager.plantFromSeed(seed.getPlantId(), tileX, tileY);
        }

        return false;
    }

    public TreeManager getTreeManager() {
        return treeManager;
    }

    public CropManager getCropManager() {
        return cropManager;
    }

    @Override
    public boolean onSeedSelected(SeedItem seedItem) {
        try {
            float x = WorldController.getInstance().getPlayer().getPosition().x;
            float y = WorldController.getInstance().getPlayer().getPosition().y;
            int tileX = (int) (x / gameMap.getTileWidth());
            int tileY = (int) (y / gameMap.getTileHeight());
            PlayerDirection dir = WorldController.getInstance().getPlayer().getDirection();
            switch (dir) {
                case LEFT:
                    tileX--;
                    break;
                case RIGHT:
                    tileX++;
                    break;
                case UP:
                    tileY++;
                    break;
                case DOWN:
                    tileY--;
                    break;
            }
            selectedSeedForPlanting = seedItem;
            System.err.println(seedItem.getName() + " selected: " + tileX + "," + tileY);
            boolean success = plantSeed(seedItem, tileX, tileY);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private InventoryItem fertilizerSelected = null;

    @Override
    public boolean onFertilizerSelected(InventoryItem fertilizer) {
        try {
            float x = WorldController.getInstance().getPlayer().getPosition().x;
            float y = WorldController.getInstance().getPlayer().getPosition().y;
            int tileX = (int) (x / gameMap.getTileWidth());
            int tileY = (int) (y / gameMap.getTileHeight());
            PlayerDirection dir = WorldController.getInstance().getPlayer().getDirection();
            switch (dir) {
                case LEFT:
                    tileX--;
                    break;
                case RIGHT:
                    tileX++;
                    break;
                case UP:
                    tileY++;
                    break;
                case DOWN:
                    tileY--;
                    break;
            }
            fertilizerSelected = fertilizer;
            System.err.println(fertilizerSelected.getName() + " selected: " + tileX + "," + tileY);
            boolean success = fertilize(fertilizerSelected, tileX, tileY);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean fertilize(InventoryItem fertilizer, int tileX, int tileY) {
        GiantCrop crop = cropManager.getGiantCropAt(tileX, tileY);
        if (crop != null) {
            crop.fertilize(fertilizer.getName());
            return true;
        } else if (cropManager.getCropAt(tileX, tileY) != null) {
            cropManager.getCropAt(tileX, tileY).fertilize(fertilizer.getName());
            return true;
        }
        return false;
    }
}
