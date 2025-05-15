package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.DayWithoutWaterReach2;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;
import models.MapElements.Tile.Tile;
import managers.Progress.Farming.TreeGrow;
import models.MapElements.crops.Tree.TreeInMap;

import java.util.Arrays;

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
    public void removeSeasonalCrop(SeasonChangedEvent event) {
        if (Arrays.stream(tree.getTreeInfo().getSeason()).
                noneMatch(season -> season == event.newSeason()) &&
                !tree.isHarvestAble()) {
            removeThisFeature();
        }
    }

    @Subscribe
    public void removeTree(DayWithoutWaterReach2 event) {
        if (this.tile.getX() == event.x() && this.tile.getY() == event.y()) {
            tile.removeFeature(hasPlant.class);
            //make sure tile display changed after this
        }
    }

    private void removeThisFeature() {
        tile.removeFeature(hasTree.class);
        tile.addFeature(isEmpty.class, new isEmpty());
        tile.removeFeature(canWater.class);
        tile.setSymbol('.');
    }
}
