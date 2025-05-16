package models.MapElements.crops.Tree;

import models.MapElements.Tile.TileFeatures.TileFeature;
import models.Player;

public class TreeInMap implements TileFeature {
    private final TreeInfo tree;
    private boolean completeGrow = false;
    private boolean isForaging;
    private boolean harvestAble = false;
    private final int[] growStages;
    private boolean fertilized = false;
    int daysInCycle = 0;
    int currentStage = 0;
    int daysInStage = 0;

    public TreeInMap(TreeInfo tree) {
        this.tree = tree;
        growStages = tree.getStages();
        //    seasons = tree.getSeason();
    }

    public TreeInfo getTreeInfo() {
        return tree;
    }

    public boolean isHarvestAble() {
        return harvestAble;
    }

    public boolean isFertilized() {
        return fertilized;
    }

    public void fertilize() {
        fertilized = true;
    }
    public boolean isCompleteGrow() {
        return completeGrow;
    }

    public void advanceDayInStage() {
        if (!completeGrow) {
            daysInStage++;
            if (daysInStage == growStages[currentStage]) {
                advanceStage();
                daysInStage = 0;
            }
        } else if(!harvestAble) {
            daysInCycle++;
            if (daysInCycle == tree.getFruit().getHarvestCycle()){
                harvestAble = true;
                daysInCycle = 0;
            }
        }
    }

    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            completeGrow = true;
            harvestAble = true;
        }
    }

    public int daysRemainToCompleteGrow() {
        int days = 0;
        for (int i = currentStage; i < growStages.length; i++) {
            days += growStages[i];
        }
        days -= daysInStage;
        return days;
    }


    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }

    public void harvest(Player player) {
        tree.getFruit().saveInInventory(1, player);
        harvestAble = false;
        daysInCycle = 0;
    }
}

