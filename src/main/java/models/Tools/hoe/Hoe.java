package models.Tools.hoe;

import models.Tools.Tool;
import models.User;

public class Hoe extends Tool {
    private HoeLevel level;

    public Hoe(HoeLevel level) {
        super(level.getName(), level.getEnergy());
        this.level = level;
    }

    @Override
    public void useTool() {
        if (User.getEnergy < level.getEnergy()) {
            //can't continue without energy

        }
        User.decreaseEnergy(level.getEnergy());

    }

    public HoeLevel getLevel() {
        return level;
    }
}
