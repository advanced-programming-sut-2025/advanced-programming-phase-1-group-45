package models.MapElements.crops;

import models.MapElements.crops.Tree.TreeInfo;
import models.MapElements.crops.Tree.Wood;

public class ForagingTree {
    private TreeInfo tree;
    private final Wood wood;

    public ForagingTree(TreeInfo tree) {
        this.tree = tree;
        tree.setForaging(true);
        wood = new Wood(tree, true);
    }

    public Wood getWood() {
        return wood;
    }
}
