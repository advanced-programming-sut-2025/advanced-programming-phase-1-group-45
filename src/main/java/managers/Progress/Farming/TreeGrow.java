package managers.Progress.Farming;

import managers.TimeManager;
import models.MapElements.crops.Tree.TreeInMap;

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
