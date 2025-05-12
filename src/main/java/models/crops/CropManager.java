package models.crops;

import com.sun.scenario.effect.Crop;
import models.Enums.Tile;
import models.Events.GameEventBus;

public class CropManager {
    private Tile tile;
    private CropInfo crop;

    CropManager(Tile tile, Crop crop) {
        this.tile = tile;
        GameEventBus.INSTANCE.register(this);
    }

}
