package models.Tools;

import models.User;

public class MilkPail extends Tool {

    public MilkPail(int energy) {
        super("MilkPail", energy);
    }

    @Override
    public void useTool() {
        User.decreaseEnergy(4);
    }
}
