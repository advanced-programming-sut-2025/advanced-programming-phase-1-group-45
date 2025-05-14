package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.MapElements.crops.Tree.TreeGrow;
import models.MapElements.crops.Tree.TreeInMap;

public class hasTree implements TileFeature {
    private final Tile tile;
    private TreeInMap tree;
    private TreeGrow treeGrow;

    public hasTree(Tile tile, TreeInMap tree) {
        this.tile = tile;
        this.tree = tree;
        treeGrow = new TreeGrow();
        tile.setSymbol('T');
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
            tile.removeFeature(hasPlant.class);
            //make sure tile display changed after this
        }
    }
}
