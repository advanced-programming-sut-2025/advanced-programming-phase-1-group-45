package models.Tools;

import models.GameSession;

import java.util.Map;

public class ToolManager {
    public void toolEquip(String toolName) {
        GameSession.getCurrentPlayer().equipTool(toolName);
    }
    public void toolShowCurrent(){
        Class<?> currentTool = GameSession.getCurrentPlayer().getCurrentTool();
        if(currentTool.getSuperclass() == Tool.class){
            System.out.println(currentTool.getSimpleName());
        }
    }
    public void showAllToolsAvailable(){
       Map<String, Integer> inventory = GameSession.getCurrentPlayer().getInventory();
       for(String itemName : inventory.keySet()){
           Class<?> itemClass = itemName.getClass();
           if(itemClass.getSuperclass() == Tool.class){
               System.out.println(itemClass.getSimpleName());
           }
       }
    }
}
