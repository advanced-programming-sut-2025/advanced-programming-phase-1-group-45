package models.MapElement.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.UseToolEvent;
import models.Tools.Hoe;

public class PlowSituation implements TileFeature {
    private boolean isPlowed = false;

    public void plow() {
        this.isPlowed = true;
    }

    public void unPlow() {
        this.isPlowed = false;
    }

    public boolean isPlowed() {
        return this.isPlowed;
    }
}
