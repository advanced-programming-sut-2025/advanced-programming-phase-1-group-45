package models.Events;

import models.Tools.Tool;
import models.Tools.UpgradeAbleTool;

public class UpgradeToolEvent {
    private final UpgradeAbleTool tool;

    public UpgradeToolEvent(UpgradeAbleTool tool) {
        this.tool = tool;
    }

    public UpgradeAbleTool getTool() {
        return tool;
    }
}
