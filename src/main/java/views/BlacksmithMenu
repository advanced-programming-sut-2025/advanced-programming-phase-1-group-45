package views;

import com.sun.glass.ui.Window;
import controllers.MenuController;
import managers.PriceManager;
import models.Enums.Command;
import models.Tools.Tool;
import models.Tools.ToolLevel.ToolLevel;
import models.Tools.TrashCan;
import models.Tools.UpgradeAbleTool;

import java.util.regex.Matcher;

public class BlacksmithMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller) {
        Matcher matcher = Command.toolsUpgrade.getMatcher(command);
        String toolName = matcher.group("toolName");
        Tool tool =controller.getCurrentSession().getToolManager().
                findTool(toolName, controller.getCurrentUser().getPlayer());
        if (tool == null) {
            System.out.println("You can not upgrade this tool!");
            return;
        }
        else if(!(tool instanceof UpgradeAbleTool)){
            System.out.println("This tool is not upgradable!");
            return;
        }
        double price;
        if(tool instanceof TrashCan) {

        }
        else {
            ToolLevel newLevel = ((UpgradeAbleTool) tool).getLevel().getNextLevel();
            if (newLevel == null) {
                System.out.println("You reached the last level!");
                return;
            }
            String levelName = newLevel.getName().toLowerCase() + " Tool";
            price = PriceManager.getBasePrice(levelName);
            if(controller.getCurrentUser().getMoney() < price){
                System.out.println("You don't have enough money to upgrade this tool!");
                return;
            }
            controller.getCurrentUser().addMoney(-price);
            ((UpgradeAbleTool) tool).upgrade();
        }
    }
}
