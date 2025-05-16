package models.Tools.Backpack;

public abstract class BackPackItem {
    public abstract String getItemName();
    public abstract void saveInInventory(int amount);
}
