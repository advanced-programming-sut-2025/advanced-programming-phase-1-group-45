package models.Tools;

import models.User;

public class Scythe extends Tool {

    public Scythe(int energy) {
        super("Scythe", energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(2);
    }
}
