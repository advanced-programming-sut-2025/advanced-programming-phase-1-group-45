package managers.Progress;

import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasPlant;
import models.MapElements.Tile.TileFeatures.hasTree;

public class Farming {
    public void plant(String seed, Tile tile) {
        PlantingSystem.Plant(seed, tile);
    }

    public void fertilize(String fertilized, Tile tile) {
        if (tile.hasFeature(hasTree.class) && !tile.hasFeature(hasPlant.class)) {
            throw new IllegalStateException("You can not fertilize this tile");
        } else if ((tile.hasFeature(hasPlant.class) && tile.getFeature(hasPlant.class).getCrop().isFertilized()) ||
                (tile.hasFeature(hasTree.class) && tile.getFeature(hasTree.class).getTree().isFertilized())) {
            throw new IllegalStateException("This tile is already fertilized.");
        } else {
            if (tile.hasFeature(hasPlant.class)) {
                tile.getFeature(hasPlant.class).getCrop().fertilize();
            }
            else if (tile.hasFeature(hasTree.class)) {
                tile.getFeature(hasTree.class).getTree().fertilize();
            }
        }
    }
}
