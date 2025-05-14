package models.Tools;

import models.Tools.ToolLevel.ToolLevel;

public interface UpgradeAbleTool {

    public abstract void upgrade();

    public abstract ToolLevel getLevel();
}
