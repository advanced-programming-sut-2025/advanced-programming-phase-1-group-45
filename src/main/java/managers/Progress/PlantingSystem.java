package managers.Progress;

import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.*;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.Plant.PlantInMap;
import models.MapElements.crops.PlantSeed;
import models.MapElements.crops.Tree.TreeInMap;
import models.MapElements.crops.TreeSeed;

public class PlantingSystem {
    public static void Plant(String seed, Tile tile) {
        if (!tile.hasFeature(canPlant.class)) {
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
        if (!hasInInventory(seed)) {
            throw new IllegalStateException("You don't have this seed in your inventory.");
        }
        hasTree tree = new hasTree(tile, new TreeInMap(seed.getTree()));
        tile.addFeature(tree.getClass(), tree);
        tile.addFeature(canWater.class, new canWater(tile));
        tile.removeFeature(canPlant.class);
        tile.getFeature(PlowSituation.class).unPlow();
    }

    private static void PlantAPlant(PlantSeed seed, Tile tile) {
        if (!hasInInventory(seed)) {
            throw new IllegalStateException("You don't have this seed in your inventory.");
        }
        hasPlant plant = new hasPlant(tile, new PlantInMap(seed.getPlant()));
        tile.addFeature(plant.getClass(), plant);
        tile.addFeature(canWater.class, new canWater(tile));
        tile.removeFeature(canPlant.class);
        tile.getFeature(PlowSituation.class).unPlow();
    }
}
