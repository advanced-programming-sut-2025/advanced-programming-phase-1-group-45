package managers.Progress.Farming;

import com.google.common.eventbus.Subscribe;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasPlant;
import models.MapElements.Tile.TileFeatures.hasTree;

import java.util.List;

public class GrowManager {
    GrowManager() {
        GameEventBus.INSTANCE.register(this);
    }
    @Subscribe
    public void updateGrowth(List<Tile> tiles, DayChangedEvent event) {
        for (Tile tile : tiles) {
            if (tile.hasFeature(hasPlant.class)) {
                hasPlant crop = tile.getFeature(hasPlant.class);
                crop.getCropGrowStrategy().grow(crop.getCrop());
                if(crop.getCrop().isHarvestAble()) {
                    tile.setSymbol('P');
                }
            } else if (tile.hasFeature(hasTree.class)) {
                tile.getFeature(hasTree.class);
                hasTree tree = tile.getFeature(hasTree.class);
                tree.getTreeGrow().grow(tree.getTree());
                if(tree.getTree().isHarvestAble()) {
                    tile.setSymbol('T');
                }
            }
        }
    }
}
