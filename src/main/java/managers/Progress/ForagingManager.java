package managers.Progress;

import com.google.common.eventbus.Subscribe;
import managers.TimeManager;
import models.Enums.Season;
import models.Events.DayChangedEvent;
import models.Events.GameEventBus;
import models.GameMap;
import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileFeatures.*;
import models.MapElements.crops.AllCropsLoader;
import models.MapElements.crops.ForagingCrop;
import models.MapElements.crops.ForagingMineral;
import models.MapElements.crops.ForagingSeed;

import java.util.*;

public class ForagingManager {
    private final double chance = 0.01;
    private int numOfNewTile;
    private Tile[][] grid;

    ForagingManager() {
        GameEventBus.INSTANCE.register(this);
        numOfNewTile = chance * GameMap.getSize() * GameMap.getSize();
        grid = GameMap.getMap();
    }

    @Subscribe
    public void placeDaily(DayChangedEvent event) {
        int numberOfForagedTiles = numOfForagedTiles();
        //at most 30% of tiles can be foraged
        numOfNewTile = Math.min(numOfNewTile, (int) (0.3 * grid.length * grid[0].length - numberOfForagedTiles));
        List<Tile> newTiles = randomSelectTile();
        for (int i = 0; i < Math.max(newTiles.size() - 1, 0); i++) {
            Tile tile = newTiles.get(i);
            tile.addFeature(hasForaging.class,
                    new hasForagingCrop(getRandomForagingCrop(), tile));
        }
        if (!newTiles.isEmpty()) {
            Tile tile = newTiles.get(newTiles.size() - 1);
            tile.addFeature(hasForaging.class, new hasForagingSeed(getForagingTreeSeed(), tile));
        }
    }

    public List<Tile> randomSelectTile() {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < numOfNewTile; i++) {
            int x, y;
            do {
                x = new Random().nextInt(grid.length);
                y = new Random().nextInt(grid[0].length);
            } while (grid[x][y].hasFeature(isEmpty.class));
            Tile newTile = grid[x][y];
            tiles.add(newTile);
        }
        return tiles;
    }

    public int numOfForagedTiles() {
        int numOfForagedTiles = 0;
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Tile tile = grid[x][y];
                if (tile.hasFeature(hasForaging.class)) {
                    numOfForagedTiles++;
                }
            }
        }
        return numOfForagedTiles;
    }

    public ForagingSeed getRandomForagingSeed() {
        Season currentSeason = TimeManager.getInstance().getSeason();
        List<ForagingSeed> seasonal = AllCropsLoader.allForagingSeeds.stream().
                filter(seed -> Arrays.asList(seed.getSeasons()).contains(currentSeason)).
                toList();
        int random = new Random().nextInt(seasonal.size() - 1);
        return seasonal.get(random);
    }

    public ForagingCrop getRandomForagingCrop() {
        Season currentSeason = TimeManager.getInstance().getSeason();
        List<ForagingCrop> seasonal = AllCropsLoader.allForagingCrops.stream().
                filter(crop -> Arrays.asList(crop.getSeasons()).contains(currentSeason)).
                toList();
        int random = new Random().nextInt(seasonal.size() - 1);
        return seasonal.get(random);
    }

    public ForagingSeed getForagingTreeSeed() {
        ForagingSeed seed;
        do {
            seed = getRandomForagingSeed();
        } while (AllCropsLoader.getInstance().findTreeSeedByName(seed.getName()) != null);
        return seed;
    }

    public ForagingMineral getRandomForagingMineral() {
        int random = new Random().nextInt(AllCropsLoader.allForagingMinerals.size());
        return AllCropsLoader.allForagingMinerals.get(random);
    }
}
