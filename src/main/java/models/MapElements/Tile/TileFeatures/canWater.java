package models.MapElements.Tile.TileFeatures;

import com.google.common.eventbus.Subscribe;
import controllers.WeatherController;
import models.Enums.Weather;
import models.Events.*;
import models.MapElements.Tile.Tile;

public class canWater implements TileFeature {
    private final Tile tile;
    private boolean isWateredToday = false;
    private int daysWithoutWater;

    public canWater(Tile tile) {
        this.tile = tile;
        daysWithoutWater = 0;
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void increaseDaysWithoutWater(DayChangedEvent event) {
        if(WeatherController.getInstance().getCurrentWeather().equals(Weather.RAINY)){
            isWateredToday = true;
            daysWithoutWater = 0;
        } else {
            isWateredToday = false;
            daysWithoutWater++;
        }
        if (daysWithoutWater == 2) {
            GameEventBus.INSTANCE.post(new DayWithoutWaterReach2(tile.getX(), tile.getY()));
            daysWithoutWater = 0;
        }
    }

    public void water() {
            this.isWateredToday = true;
            daysWithoutWater = 0;
    }

    public boolean isWateredToday() {
        return isWateredToday;
    }

    public void setWateredToday(boolean wateredToday) {
        isWateredToday = wateredToday;
    }
}
