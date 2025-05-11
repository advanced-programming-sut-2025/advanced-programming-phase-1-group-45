package models.Tools;

import models.User;

public class Scythe extends Tool {

    public Scythe(String itemName, int energy) {
        super(itemName, energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(2);
    }
}
