package models.Tools;

import models.Item;
import models.MapElements.Tile.Tile;
import models.Tools.Backpack.BackPackItem;

public abstract class Tool implements BackPackItem {
    protected String toolName;
    protected int energy;

    public Tool(String itemName, int energy) {
        this.toolName = itemName;
        this.energy = energy;
    }

    public abstract void useTool(Tile targetTile);
    public abstract void decreaseEnergy();


    public String getName() {
        return toolName;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public BackPackItem clone() {}

}
