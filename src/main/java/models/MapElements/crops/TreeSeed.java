package models.MapElements.crops;

import models.MapElements.crops.Tree.TreeInfo;
import models.Tools.Backpack.BackPackItem;

public class TreeSeed extends BackPackItem {
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

    @Override
    public String getItemName() {
        return this.name;
    }

    @Override
    public void saveInInventory(int amount) {
        Player.getInventory().addItem(this);
    }
}
