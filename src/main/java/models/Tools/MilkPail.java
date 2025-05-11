package models.Tools;

import models.User;

public class MilkPail extends Tool {

    public MilkPail(String itemName, int energy) {
        super(itemName, energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(4);
    }
}
