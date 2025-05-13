package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.crops.Crop.CropGrow;
import models.crops.Tree.TreeGrow;
import models.crops.Tree.TreeInMap;
import models.crops.Tree.TreeInfo;

public class hasTree implements TileFeature, UnWalkAble {
    private final Tile tile;
    private TreeInMap tree;
    private TreeGrow treeGrow;

    hasTree(Tile tile, TreeInMap tree) {
        this.tile = tile;
        this.tree = tree;
        CropGrow cropGrowStrategy = new CropGrow();
        GameEventBus.INSTANCE.register(this);
    }
    public TreeInMap getTree() {
        return tree;
    }
    public TreeGrow getTreeGrow() {
        return treeGrow;
    }

    @Subscribe
    public void removeTree(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y()) {
            tile.removeFeature(hasCrop.class);
            //make sure tile display changed after this
        }
    }
}
