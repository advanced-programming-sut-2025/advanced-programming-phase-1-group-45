package models.MapElements.crops.Tree;

import models.GameSession;
import models.Player;
import models.Tools.Backpack.BackPackItem;

public class Wood extends BackPackItem {
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
    public void saveInInventory(int amount, Player player) {
        player.getBackpack().addItemAmount(this, amount);
    }
}
