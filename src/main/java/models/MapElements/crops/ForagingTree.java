package models.MapElements.crops;

import models.MapElements.crops.Tree.TreeInfo;
import models.MapElements.crops.Tree.Wood;
import models.Player;

import java.util.Arrays;

public class ForagingTree {
    private TreeInfo tree;
    private final Wood wood;

    public ForagingTree(TreeInfo tree) {
        this.tree = tree;
        tree.setForaging(true);
        wood = new Wood(tree, true);
        growStages = tree.getStages();
    }

    public Wood getWood() {
        return wood;
    }
    @Override
    public String toString() {
        return tree.getName();
    }
    private boolean completeGrow = false;
    private boolean harvestAble = false;
    private final int[] growStages;
    private boolean fertilized = false;
    int currentStage = 0;
    int daysInStage = 0;

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

    public void advanceStage() {
        if (currentStage < growStages.length) {
            currentStage++;
        }
        if (currentStage == growStages.length) {
            completeGrow = true;
            harvestAble = true;
        }
    }


    public int getDaysInCurrentStage() {
        return growStages[currentStage];
    }

    public void harvest(Player player) {
        tree.getFruit().saveInInventory(1, player);
        harvestAble = false;
    }
}
