package models.Tools.Backpack;

import com.google.common.eventbus.Subscribe;
import models.Events.GameEventBus;
import models.Events.UpgradeToolEvent;
import models.Item;
import models.Tools.Tool;
import models.Tools.UpgradeAbleTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Backpack {

    private BackpackType backpackType;
    private HashMap<Item, Integer> items;
    private List<Tool> tools;

    public Backpack(int capacity) {
        this.backpackType = BackpackType.BASIC;
        this.items = new HashMap<>();
        this.tools = new ArrayList<>();
        GameEventBus.INSTANCE.register(this);
    }

    public void addItemAmount(Item item, int amount) {
        if (backpackType.canAddItem(items.size())) {
            if (items.containsKey(item)) {
                items.put(item, items.get(item) + amount);
            } else {
                items.put(item, amount);
            }
        } else {
            throw new IllegalArgumentException("Backpack is full");
        }
    }

    public boolean removeItem(Item item, int amount) {
        int current = items.getOrDefault(item, 0);
        if (current >= amount) {
            items.put(item, current - amount);
            return true;
        }
        return false;
    }

    public boolean hasItem(String item) {
        return getNumberOfAnItem(item) > 0;
    }

    public int getNumberOfAnItem(String itemName) {
        for (Item item : items.keySet()) {
            if (item.getItemName().equals(itemName)) {
                return items.get(item);
            }
        }
        return 0;
    }

    public String getName() {
        return backpackType.getName();
    }

    public int getCapacity() {
        return backpackType.getCapacity();
    }

    public void upgradeBackpack() {
        this.backpackType = backpackType.upgrade();
    }

    public void addTool(Tool tool) {
        tools.add(tool);
    }

    @Subscribe
    public void onToolUpgrade(UpgradeToolEvent event) {
        int toolIndex = tools.indexOf(event.getTool());
        UpgradeAbleTool oldTool = (UpgradeAbleTool) tools.get(toolIndex);
        oldTool.upgrade();
        System.out.println(tools.get(toolIndex).getName() + " upgraded to " +
                event.getTool().getLevel() + " level.");
    }

}
