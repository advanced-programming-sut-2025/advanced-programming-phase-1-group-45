package models.Tools;

import models.User;

public class Shear extends Tool{
    public Shear(int energy) {
        super("Shear", energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(4);
    }
}
