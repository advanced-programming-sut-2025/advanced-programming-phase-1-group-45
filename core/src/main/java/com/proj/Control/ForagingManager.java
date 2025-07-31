package com.proj.Control;

import com.badlogic.gdx.utils.Array;
import com.proj.Model.mapObjects.ForagingItem;
import com.proj.Model.GameAssetManager;
import com.proj.map.GameMap;
import com.proj.map.Season;

import java.awt.*;
import java.util.Random;

public class ForagingManager {
    private Array<ForagingItem> foragingCrops = new Array<>();
    private GameMap currentMap;
    private final Random random = new Random();

    public ForagingManager() {
        foragingCrops = GameAssetManager.getGameAssetManager().getForagingCrops();
    }

    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
    }


    public void spawnDailyItems(Season currentSeason) {
        currentMap.removeForagings();
        Array<ForagingItem> seasonalItems = new Array<>();
        for (ForagingItem item : foragingCrops) {
            if (item.getSeason().contains(currentSeason)) {
                seasonalItems.add(item);
            }
        }
        int totalTiles = currentMap.getMapWidth() * currentMap.getMapHeight();
        int itemsToSpawn = Math.max(1, (int) (totalTiles * 0.005));
        for (int i = 0; i < itemsToSpawn; i++) {
            Point position = findValidSpawnPosition();
            ForagingItem template = seasonalItems.random();

            ForagingItem newItem = new ForagingItem(
                template.getName(),
                template.getSeason().toArray(new Season[0]),
                template.getBaseSellPrice(),
                template.getEnergy(),
                template.getTexture()
            );
            System.err.println(newItem.getName());
            newItem.setPosition(position);
            currentMap.putForagingInTile(position.x, position.y, newItem);
        }
    }

    private Point findValidSpawnPosition() {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(currentMap.getMapWidth());
            int y = random.nextInt(currentMap.getMapHeight());
            if (currentMap.canPlantInTile(x, y)) {
                return new Point(x, y);
            }
            attempts++;
        }
        return new Point(0, 0);
    }

    public ForagingItem tryCollectItem(Point playerTilePosition, String toolName) {
        return currentMap.harvestForagingItem(playerTilePosition, toolName);
    }


}
