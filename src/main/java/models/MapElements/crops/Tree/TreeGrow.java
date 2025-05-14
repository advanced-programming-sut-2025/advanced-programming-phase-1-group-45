package models.MapElement.crops.Tree;

import managers.TimeManager;

import java.util.Arrays;

public class TreeGrow {
    public void grow(TreeInMap tree) {
        if (Arrays.stream(tree.getTreeInfo().getSeason()).
                noneMatch(season -> season == TimeManager.getInstance().getSeason())) {
            return;
        }
        if (!tree.isHarvestAble()) {
            tree.advanceDayInStage();
        }
    }
}
