package models.MapElement.crops.Tree;

import models.MapElements.Tile.TileFeatures.canGrow;

public class TreeInMap extends canGrow {
    private final TreeInfo tree;
    private boolean completeGrow = false;
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

    @Override
    public void advanceDayInStage() {
        if (!completeGrow) {
            daysInStage++;
            if (daysInStage == growStages[currentStage]) {
                advanceStage();
                daysInStage = 0;
            }
        } else {
            daysInCycle++;
            if (daysInCycle == tree.getFruit().getHarvestCycle()){
                harvestAble = true;
                daysInCycle = 0;
            }
        }
    }

    @Override
    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            completeGrow = true;
            harvestAble = true;
        }
    }

    @Override
    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }
}
