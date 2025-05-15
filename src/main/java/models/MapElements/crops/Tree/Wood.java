package models.MapElements.crops.Tree;

import models.GameSession;
import models.Tools.Backpack.BackPackItem;

public class Wood implements BackPackItem {
    private final TreeInfo tree;

    public Wood(TreeInfo tree) {
        this.tree = tree;
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
