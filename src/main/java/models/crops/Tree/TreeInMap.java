package models.crops.Tree;

import models.MapElements.Tile.TileFeatures.canGrow;

public class TreeInMap extends canGrow {
    private TreeInfo tree;
    private boolean harvestAble = false;
    private final int[] growStages;
 //   private final Season[] seasons;
    int currentStage = 0;
    int daysInStage = 0;

    TreeInMap(TreeInfo tree) {
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

    @Override
    public void advanceDayInStage() {
        daysInStage++;
        if (daysInStage == growStages[currentStage]) {
            advanceStage();
            daysInStage = 0;
        }
    }

    @Override
    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            harvestAble = true;
        }
    }

    @Override
    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }
}
