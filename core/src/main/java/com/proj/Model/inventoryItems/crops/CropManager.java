package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.map.GameMap;
import com.proj.map.Season;

public class CropManager {
    private final Array<Crop> crops = new Array<>();
    private GameMap map;

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean plantFromSeed(String cropId, int tileX, int tileY) {
        CropData data = CropRegistry.getInstance().get(cropId);
        if (data == null) {
            System.out.println("Crop " + cropId + " not found");
            return false;}

        if (!map.canPlantInTile(tileX, tileY)) {
            return false;
        }

        crops.add(new Crop(data, tileX, tileY));
        map.getTile(tileX, tileY).setOccupied(true);
        map.getTile(tileX, tileY).setPassable(false);
        return true;
    }

    public void updateDaily(Season season) {
        for (Crop crop : crops) {
            if (crop.getCropData().getSeason().contains(season, false)) {
                crop.grow();
            }

            if (crop.isFullyGrown() &&
                crop.getCropData().getSeason().contains(season, false) &&
                !crop.hasProduct()) {
                crop.setHasProduct(true);
            }
        }
    }

    public InventoryItem harvestAt(int tileX, int tileY) {
        Crop crop = getCropAt(tileX, tileY);
        if (crop == null || !crop.hasProduct()) return null;

        return crop.harvest();
    }

    public InventoryItem chopAt(int tileX, int tileY) {
        Crop crop = getCropAt(tileX, tileY);
        if (crop == null) return null;

        InventoryItem item = crop.chop();
        map.getTile(tileX, tileY).setOccupied(false);
        map.getTile(tileX, tileY).setPassable(true);
        crops.removeValue(crop, true);
        return item;
    }

    public void renderAll(SpriteBatch batch, Season season) {
        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        for (Crop crop : crops) {
            TextureRegion tex = crop.getTexture();;
            if (tex == null) {
                continue;}

            float texW = tex.getRegionWidth() / 2f ;
            float texH = tex.getRegionHeight() / 2f;

            float drawX = crop.getX() * tileW + (tileW - texW) / 2f;
            float drawY = crop.getY() * tileH;
            batch.draw(
                tex,
                drawX, drawY,
                texW, texH
            );
        }
    }


    public Crop getCropAt(int x, int y) {
        for (Crop crop : crops) {
            if (crop.getX() == x && crop.getY() == y) {
                return crop;
            }
        }
        return null;
    }

    public Array<Crop> getCrops() {
        return crops;
    }
}

