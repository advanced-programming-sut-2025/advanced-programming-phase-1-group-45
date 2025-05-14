package models.MapElements.crops.Tree;

import models.Tools.Backpack.BackPackItem;

public class Fruit implements BackPackItem {
    private final String name;
    private final TreeInfo tree;
    private final int harvestCycle;
    private final int BaseSellPrice;
    private final boolean isEdible;
    private final int energy;

    Fruit(String fruitName, TreeInfo tree, int harvestCycle,
          int BaseSellPrice, boolean isEdible, int energy) {
        this.name = fruitName;
        this.tree = tree;
        this.harvestCycle = harvestCycle;
        this.BaseSellPrice = BaseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        //AllCropsLoader.getInstance().addFruit(this);
    }

    public String getName() {
        return name;
    }

    public TreeInfo getTree() {
        return tree;
    }

    public int getHarvestCycle() {
        return harvestCycle;
    }

    public int getBaseSellPrice() {
        return BaseSellPrice;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public String getItemName() {
        return this.name;
    }

    @Override
    public void saveInInventory() {
        Player.getInventory().addItem(this);
    }
}
