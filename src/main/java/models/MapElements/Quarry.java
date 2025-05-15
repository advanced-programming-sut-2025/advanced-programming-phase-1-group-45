package models.MapElements;

import com.google.common.eventbus.Subscribe;
import javafx.scene.paint.RadialGradient;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.hasForaging;
import models.MapElements.Tile.TileFeatures.hasForagingMinerals;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.ForagingMineral;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quarry {
    List<Tile> tiles = new ArrayList<>();
    int daysForForageItem = 0;

    Quarry(List<Tile> tiles) {
        this.tiles = tiles;
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void spawnDailyForageMineral(DayChangedEvent event) {
        int number = Math.min((int) (tiles.size() * 0.5) - 2, 2);
        for (int i = 0; i < number; i++) {
            ForagingMineral mineral = selectRandom();
            Tile randomTile = selectRandomTile();
            randomTile.addFeature(hasForaging.class, new hasForagingMinerals(mineral, randomTile));
        }
    }

    private ForagingMineral selectRandom() {
        return AllCropsLoader.allForagingMinerals.
                get(new Random().nextInt(AllCropsLoader.allForagingMinerals.size()));
    }

    private Tile selectRandomTile() {
        Tile tile;
        do {
            tile = tiles.get(new Random().nextInt(tiles.size()));
        } while (!tile.hasFeature(hasForaging.class));
        return tile;
    }
}
