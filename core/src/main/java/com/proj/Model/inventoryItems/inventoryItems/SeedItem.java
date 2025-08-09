package com.proj.Model.inventoryItems.inventoryItems;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryItemType;
import com.proj.map.Season;

public class SeedItem extends InventoryItem {
    public enum SeedType { CROP, TREE }

    private final SeedType seedType;
    private final String plantId;
    private final Array<Season> season;

    public SeedItem(String id, String name, TextureRegion texture,
                    int quantity, int maxStackSize,
                    SeedType seedType, String plantId,
                    Array<Season> seasons) {
        super(id, name, texture, true, maxStackSize);
        setQuantity(quantity);
        setInventoryItemType(InventoryItemType.SEED);
        this.seedType = seedType;
        this.plantId = plantId;
        this.season = seasons;
    }

    public SeedType getSeedType() { return seedType; }
    public String getPlantId() { return plantId; }
    public Array<Season> getSeasons() { return season; }

    public boolean canPlantInSeason(Season currentSeason) {
        for (Season s : season) {
            if (s == currentSeason) return true;
        }
        return false;
    }

    @Override
    public void use() {
    }
}
