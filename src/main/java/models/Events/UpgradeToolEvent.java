package models.Events;

import models.Tools.Tool;

public class UpgradeToolEvent {
    private final Tool tool;

    public UpgradeToolEvent(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }
}
