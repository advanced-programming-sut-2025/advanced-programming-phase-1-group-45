package managers;

import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasCrop;
import models.MapElements.Tile.TileFeatures.hasTree;

import java.util.List;

public class GrowManager {

    public void updateGrowth(List<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile.hasFeature(hasCrop.class)) {
                hasCrop crop = tile.getFeature(hasCrop.class);
                crop.getCropGrowStrategy().grow(crop.getCrop());
            } else if (tile.hasFeature(hasTree.class)) {
                tile.getFeature(hasTree.class);
                hasTree tree = tile.getFeature(hasTree.class);
                tree.getTreeGrow().grow(tree.getTree());
            }
        }
    }
}
