package models.MapElements.Tile.TileFeatures;

import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.ForagingSeed;
import models.MapElements.crops.ForagingTree;
import models.MapElements.crops.Tree.TreeInMap;
import models.MapElements.crops.TreeSeed;
import models.Player;

import java.util.Random;

public class hasForagingSeed extends hasForaging implements TileFeature {
    private ForagingSeed seed;
    private ForagingTree tree;

    public hasForagingSeed(ForagingSeed foragingSeed, Tile tile) {
        super(tile);
        this.seed = foragingSeed;
        this.tree = new ForagingTree(AllCropsLoader.
                getInstance().findTreeSeedByName(seed.getName()).getTree());
    }

    public void chopTree(Player player) {
        tree.getWood().saveInInventory(1, player);
        AllCropsLoader.allForagingSeeds.
                get(new Random().nextInt(AllCropsLoader.allForagingSeeds.size() - 1)).
                saveInInventory(1, player);
        super.getTile().removeFeature(hasForaging.class);
        super.getTile().setSymbol('.');
    }
}
