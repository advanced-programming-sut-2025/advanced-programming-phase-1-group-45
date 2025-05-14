package models.crops;

import models.crops.Tree.TreeInfo;

public class TreeSeed {
    private final String name;
    private final TreeInfo tree;

    public TreeSeed(TreeInfo tree) {
        this.tree = tree;
        this.name = tree.getSource();
    }

    public String getName() {
        return name;
    }

    public TreeInfo getTree() {
        return tree;
    }
}
