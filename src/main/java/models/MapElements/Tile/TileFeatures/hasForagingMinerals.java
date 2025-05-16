package models.MapElements.Tile.TileFeatures;

import models.MapElements.Tile.Tile;
import models.MapElements.crops.ForagingMineral;
import models.Player;

public class hasForagingMinerals extends hasForaging implements TileFeature {
    private final ForagingMineral mineral;

    public hasForagingMinerals(ForagingMineral foragingMinerals, Tile tile) {
        super(tile);
        this.mineral = foragingMinerals;
        tile.setSymbol('+');
    }

    public void collectForagingElement(Player player) {
        mineral.saveInInventory(1, player);
        super.getTile().removeFeature(hasForaging.class);
        super.getTile().setSymbol('Q');
    }
}
