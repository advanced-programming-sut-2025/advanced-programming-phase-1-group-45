package managers;

import models.GameMap;
import models.GameSession;
import models.MapElements.Tile.Tile;
import models.Player;
import models.Tools.Direction;
import models.Tools.Tool;
import models.Tools.UpgradeAbleTool;

import java.util.List;

public class ToolManager {
    private Player player;
    private GameSession session;
    public ToolManager(GameSession gs, Player player) {
        this.player = player;
        this.session = gs;
    }
    public void toolEquip(String toolName, Player player) {
      player.equipTool(toolName);
    }

    public String toolShowCurrent(Player player) {
        return player.getCurrentTool();
    }

    public void showAllToolsAvailable(Player player) {
        List<Tool> toolInventory = player.getBackpack().getTools();
        for (Tool tool : toolInventory) {
            if (tool instanceof UpgradeAbleTool) {
                System.out.println(tool.getName() + " level: " + ((UpgradeAbleTool) tool).getLevel());
            } else {
                System.out.println(tool.getName());
            }
        }
    }

    public void toolUpgrade(String toolName, Player player) {
         if(!(findTool(toolName, player) instanceof UpgradeAbleTool)){
            System.out.println("This tool is not upgradable!");
        }
        else {
            UpgradeAbleTool tool = (UpgradeAbleTool) findTool(toolName, player);
            tool.upgrade();
        }
    }

    public void useTool(String direction, Player player) {
//        int currentX = User.getX();
//        int currentY = User.getY();
//        Tile currentTile = GameMap.getTile(x, y);
        Tool tool = findTool(player.getCurrentTool(), player);
        try {
            tool.useTool(player);
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
            tool.useTool(targetTile, player);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public Tool findTool(String toolName, Player player) {
        Tool currentTool = null;
        for (Tool tool : player.getBackpack().getTools()) {
            if (tool.getName().equals(toolName)) {
                currentTool = tool;
                break;
            }
        }
        return currentTool;
    }

    public Tile findTargetTile(String direction, Player player) {
        int newX = Direction.findDirection(direction).getX();
        int newY = Direction.findDirection(direction).getY();
        Tile targetTile = this.session.getMap().getTile(newX, newY);
        return targetTile;
    }
}
