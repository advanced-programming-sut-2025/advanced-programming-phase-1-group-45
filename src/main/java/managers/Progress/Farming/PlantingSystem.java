package managers.Progress.Farming;

import models.GameMap;
import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.*;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.Plant.PlantInMap;
import models.MapElements.crops.Plant.PlantInfo;
import models.MapElements.crops.PlantSeed;
import models.MapElements.crops.Tree.TreeInMap;
import models.MapElements.crops.TreeSeed;

import java.util.HashMap;
import java.util.List;

public class PlantingSystem {
    public static void Plant(String seed, Tile tile) {
        if (!tile.hasFeature(isEmpty.class)) {
            throw new IllegalStateException("You can not plant in this tile. Choose another tile.");
        } else if (tile.hasFeature(PlowSituation.class) && !tile.getFeature(PlowSituation.class).isPlowed()) {
            throw new IllegalStateException("You should plow this tile and then plant.");
        }
        TreeSeed seed1 = AllCropsLoader.getInstance().findTreeSeedByName(seed);
        if (seed1 != null) {
            PlantingSystem.PlantATree(seed1, tile);
        } else {
            PlantSeed seed2 = AllCropsLoader.getInstance().findPlantSeedByName(seed);
            PlantAPlant(seed2, tile);
        }
    }

    private static void PlantATree(TreeSeed seed, Tile tile) {
        if (!GameSession.getCurrentPlayer().getInventory().hasItem(seed)) {
            throw new IllegalStateException("You don't have this seed in your inventory.");
        }
        hasTree tree = new hasTree(tile, new TreeInMap(seed.getTree()));
        tile.addFeature(tree.getClass(), tree);
        tile.addFeature(canWater.class, new canWater(tile));
        tile.removeFeature(isEmpty.class);
        tile.getFeature(PlowSituation.class).unPlow();
    }

    private static void PlantAPlant(PlantSeed seed, Tile tile) {
        if (!GameSession.getCurrentPlayer().getInventory().hasItem(seed)) {
            throw new IllegalStateException("You don't have this seed in your inventory.");
        }
        hasPlant plant = new hasPlant(tile, new PlantInMap(seed.getPlant()));
        tile.addFeature(plant.getClass(), plant);
        tile.addFeature(canWater.class, new canWater(tile));
        tile.removeFeature(isEmpty.class);
        tile.getFeature(PlowSituation.class).unPlow();

//        if (seed.getPlant().isCanBecomeGiant()) {
//            HashMap<Integer, Integer> coordinate = checkCanBeGiant(tile);
//        }
    }

//    private HashMap<Integer, Integer> checkCanBeGiant(Tile tile) {
//        HashMap<Integer, Integer> coordinate = new HashMap<>();
//        int x = tile.getX();
//        int y = tile.getY();
//        PlantInfo plant = tile.getFeature(hasPlant.class).getCrop().getCropInfo();
//        int newX;
//        int newY;
//        int[] deltaX = {0, 0, 1, -1};
//        int[] deltaY = {1, -1, 0, 0};
//        for (int i = 0; i < deltaX.length; i++) {
//            newX = x + deltaX[i];
//            newY = y + deltaY[i];
//            if (!GameMap.getTile(newX, newY).hasFeature(hasPlant.class) ||
//                    !GameMap.getTile(newX, newY).getFeature(hasPlant.class).
//                            getCrop().getCropInfo().getName().equals(plant)) {
//
//
//            }
//        }
//    }
//
//    public void dfs() {
//
//    }
}
