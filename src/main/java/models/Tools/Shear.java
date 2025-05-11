package models.Tools;

import models.User;

public class Shear extends Tool{
    public Shear(String itemName, int energy) {
        super(itemName, energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(4);
    }
}
