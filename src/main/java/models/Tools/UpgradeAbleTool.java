package models.Tools;

import models.Tools.ToolLevel.ToolLevel;

public abstract class UpgradeAbleTool extends Tool {

    public UpgradeAbleTool(String itemName, int energy) {
        super(itemName, energy);
    }

    public abstract void upgrade();

    public abstract ToolLevel getLevel();

}
