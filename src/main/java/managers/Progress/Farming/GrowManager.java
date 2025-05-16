package managers.Progress.Farming;

import com.google.common.eventbus.Subscribe;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasForaging;
import models.MapElements.Tile.TileFeatures.hasForagingSeed;
import models.MapElements.Tile.TileFeatures.hasPlant;
import models.MapElements.Tile.TileFeatures.hasTree;
import models.Player;

import java.util.List;

public class GrowManager {
    private Player player;
    GrowManager(Player player) {
        GameEventBus.INSTANCE.register(this);
        this.player = player;
    }
    @Subscribe
    public void updateGrowth(DayChangedEvent event) {
        Tile[][] tiles = player.getGameMap().getMap();
        for (int i = 0; i < tiles.length; i++) {
            for (Tile tile : tiles[i]) {
                if (tile.hasFeature(hasPlant.class)) {
                    hasPlant crop = tile.getFeature(hasPlant.class);
                    crop.getCropGrowStrategy().grow(crop.getCrop());
                    if (crop.getCrop().isHarvestAble()) {
                        tile.setSymbol('P');
                    }
                } else if (tile.hasFeature(hasTree.class)) {
                    tile.getFeature(hasTree.class);
                    hasTree tree = tile.getFeature(hasTree.class);
                    tree.getTreeGrow().grow(tree.getTree());
                    if (tree.getTree().isHarvestAble()) {
                        tile.setSymbol('T');
                    }
                } else if (tile.hasFeature(hasForaging.class)&&
                        (tile.getFeature(hasForaging.class) instanceof hasForagingSeed)) {
                    hasForagingSeed crop = (hasForagingSeed) tile.getFeature(hasForaging.class);
                    crop.getTreeGrow().grow(crop.getTree());
                    if (crop.getTree().isHarvestAble()) {
                        tile.setSymbol('F');
                    }
                }
            }
        }
    }
}
