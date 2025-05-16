package managers.Progress.Farming;

import models.GameSession;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.*;
import models.MapElements.crops.ForagingSeed;
import models.MapElements.crops.ForagingTree;
import models.MapElements.crops.Tree.TreeInMap;
import models.Player;

public class FarmingManager {
    private Player player;
    GrowManager growManager;
    public FarmingManager(Player player) {
        this.player = player;
        growManager = new GrowManager(player);
    }
    public void plant(String seed, Tile tile) {
        PlantingSystem.Plant(seed, tile, player);
    }

    public String showPlant(Tile tile) {
        StringBuilder sb = new StringBuilder();
        if(tile.hasFeature(hasForaging.class) && (tile.getFeature(hasForaging.class) instanceof hasForagingSeed)) {
            ForagingTree tree = ((hasForagingSeed) tile.getFeature(hasForaging.class)).getTree();
            sb.append(tree.getTreeInfo().getName() + "foraging tree\n").
            append(tree.isFertilized() ? "fertilized" : "unfertilized\n");
            sb.append(tree.isHarvestAble() ? "harvestable" : "unharvestable\n");
            sb.append(tree.getDaysInCurrentStage() + " days remain to harvest\n");
        } else if(tile.hasFeature(hasTree.class)) {
            TreeInMap tree = ((hasTree) tile.getFeature(hasTree.class)).getTree();
            sb.append(tree.getTreeInfo().getName() + "\n");
            sb.append(tree.isHarvestAble() ? "harvestable" : "unharvestable\n");
            sb.append(tree.getDaysInCurrentStage() + " days remain to harvest\n");
            sb.append(tree.isFertilized() ? "fertilized" : "unfertilized\n");
            if(tile.hasFeature(canWater.class)) {
                sb.append("is watered today" +tile.getFeature(canWater.class).isWateredToday() + "\n");
            }
            return sb.toString();
        } else if(tile.hasFeature(hasPlant.class)) {
            hasPlant hasPlant =  tile.getFeature(hasPlant.class);;
            sb.append("Name " + hasPlant.getCrop().getCropInfo().getName() + "\n");
            sb.append(hasPlant.getCrop().isFertilized() ? "fertilized" : "unfertilized\n");
            sb.append(hasPlant.getCrop().getDaysInCurrentStage() + " days remain to plant\n");
            sb.append(hasPlant.getCrop().isHarvestAble() ? "harvestable" : "unharvestable\n");
            if (tile.hasFeature(canWater.class)) {
                sb.append("is watered today" +tile.getFeature(canWater.class).isWateredToday() + "\n");
            }
            return sb.toString();
        }
        sb.append("there is no plant in this tile\n");
        return sb.toString();
    }

    public void fertilize(String fertilized, Tile tile) {
        if (!tile.hasFeature(hasTree.class) && !tile.hasFeature(hasPlant.class)) {
            System.out.println("You can not fertilize this tile");
            return;
        } else if ((tile.hasFeature(hasPlant.class) && tile.getFeature(hasPlant.class).getCrop().isFertilized()) ||
                (tile.hasFeature(hasTree.class) && tile.getFeature(hasTree.class).getTree().isFertilized())) {
            System.out.println("This tile is already fertilized.");
            return;
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
