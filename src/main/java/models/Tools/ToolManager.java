package models.Tools;

import models.GameSession;

import java.util.List;
import java.util.Map;

public class ToolManager {
    public void toolEquip(String toolName) {
        GameSession.getCurrentPlayer().equipTool(toolName);
    }

    public void toolShowCurrent() {
        Class<?> currentTool = GameSession.getCurrentPlayer().getCurrentTool();
        if (currentTool.getSuperclass() == Tool.class) {
            System.out.println(currentTool.getSimpleName());
        }
    }

    public void showAllToolsAvailable() {
        List<Tool> toolInventory = GameSession.getCurrentPlayer().getTools();
        for (Tool tool : toolInventory) {
            if (tool instanceof UpgradeAbleTool) {
                System.out.println(tool.getName() + " level: " + ((UpgradeAbleTool) tool).getLevel());
            } else {
                System.out.println(tool.getName());
            }
        }
    }
}
