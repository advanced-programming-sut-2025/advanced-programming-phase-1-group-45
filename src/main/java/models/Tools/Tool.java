package models.Tools;

import models.Item;

public abstract class Tool extends Item {
    protected String toolName;
    protected int energy;

    public Tool(String itemName, int energy) {
        super(itemName, Tool.class);
        this.energy = energy;
    }

    public abstract String useTool();

    public String getName() {
        return toolName;
    }

    public int getEnergy() {
        return energy;
    }
}
