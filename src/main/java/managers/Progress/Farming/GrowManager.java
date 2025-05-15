package managers.Progress.Farming;

import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasPlant;
import models.MapElements.Tile.TileFeatures.hasTree;

import java.util.List;

public class GrowManager {

    public void updateGrowth(List<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.hasFeature(hasPlant.class)) {
                hasPlant crop = tile.getFeature(hasPlant.class);
                crop.getCropGrowStrategy().grow(crop.getCrop());
            } else if (tile.hasFeature(hasTree.class)) {
                tile.getFeature(hasTree.class);
                hasTree tree = tile.getFeature(hasTree.class);
                tree.getTreeGrow().grow(tree.getTree());
            }
        }
    }
}
