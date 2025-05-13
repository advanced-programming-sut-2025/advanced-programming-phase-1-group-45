package models.crops;

import models.Item;

public class TreeInfo extends Item {
    private int[] stages;
    public TreeInfo(String itemName, Class<?> itemSuperClass) {
        super(itemName, itemSuperClass);
    }
}
