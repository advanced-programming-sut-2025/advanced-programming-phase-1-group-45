package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Events.UseToolEvent;
import models.Tools.Hoe;

public class PlowSituation implements TileFeature {
    private boolean isPlowed = false;

    @Subscribe
    public void plow(UseToolEvent event) {
        if (event.tool().getClass().equals(Hoe.class)) {
            this.isPlowed = true;
        }
    }

    public void unPlow() {
        this.isPlowed = false;
    }

    public boolean isPlowed() {
        return this.isPlowed;
    }
}
