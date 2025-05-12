package models.Tools;

import models.Enums.Tile;
import models.GameMap;
import models.GameSession;
import models.User;

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

    public void useTool(String toolName, String direction) {
        int currentX = User.getX();
        int currentY = User.getY();
        Tile currentTile = GameMap.getTile(x, y);
        Tool tool = findTool(toolName);
        if (tool == null) {
            System.out.println("You do not have " + toolName + " in your backpack.");
            return;
        }
        try {
            tool.decreaseEnergy();
        } catch (IllegalArgumentException exception) {
            //the player has not enough energy to use this tool
            System.out.println(exception.getMessage());
            return;
        }

        Tile targetTile = findTargetTile(direction);

        if (targetTile == null) {
            System.out.println("Target tile is out of bounds");
            return;
        }
        try {
            tool.useTool(targetTile);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private Tool findTool(String toolName) {
        Tool currentTool = null;
        for (Tool tool : User.Inventory.tools) {
            if (tool.getName().equals(toolName)) {
                currentTool = tool;
                break;
            }
        }
        return currentTool;
    }

    private Tile findTargetTile(String direction) {
        int newX = Direction.findDirection(direction).getX();
        int newY = Direction.findDirection(direction).getY();
        Tile targetTile = GameMap.getTile(newX, newY);
        return targetTile;
    }
}
