package models.MapElements.crops.Tree;

import models.GameSession;
import models.Tools.Backpack.BackPackItem;

public class Wood implements BackPackItem {
    private final TreeInfo tree;
    private boolean isForaging = false;

    public Wood(TreeInfo tree, boolean isForaging) {
        this.tree = tree;
        this.isForaging = isForaging;
    }

    public TreeInfo getTree() {
        return tree;
    }

    @Override
    public String getItemName() {
        return tree.getName() + " Wood";
    }

    @Override
    public void saveInInventory(int amount) {
        GameSession.getCurrentPlayer().getInventory().addItem(this, amount);
    }
}
