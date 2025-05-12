package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import models.Enums.Weather;
import models.Events.*;
import models.MapElements.Tile.Tile;
import models.Tools.Hoe;
import models.Tools.WateringCan;

public class canWater implements TileFeature {
    private final Tile tile;
    private boolean isWater;
    private int daysWithoutWater;

    canWater(Tile tile) {
        this.tile = tile;
        daysWithoutWater = 0;
        isWater = false;
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void increaseDaysWithoutWater(DayChangedEvent event) {
        daysWithoutWater++;
        if (daysWithoutWater == 2) {
            GameEventBus.INSTANCE.post(new DayWithoutWaterReach2(tile.getX(), tile.getY()));
        }
    }

    @Subscribe
    public void water(UseToolEvent event) {
        if (event.tool().getClass().equals(WateringCan.class)) {
            this.isWater = true;
            daysWithoutWater = 0;
        }
    }

    @Subscribe
    public void watering(WeatherChangedEvent event) {
        if (event.newWeather().equals(Weather.RAINY)) {
            this.isWater = true;
            daysWithoutWater = 0;
        }
    }

    public void unPlow() {
        this.isWater = false;
    }

    public boolean isPlowed() {
        return this.isWater;
    }
}
