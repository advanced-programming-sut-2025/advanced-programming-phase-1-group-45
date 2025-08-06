package com.proj.Model.inventoryItems.inventoryItems.crops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.crops.Crop;
import com.proj.Model.inventoryItems.crops.CropData;
import com.proj.Model.inventoryItems.crops.CropRegistry;
import com.proj.Model.inventoryItems.crops.GiantCrop;
import com.proj.map.GameMap;
import com.proj.map.Season;

import java.awt.*;

public class CropManager {
    private final Array<com.proj.Model.inventoryItems.crops.Crop> crops = new Array<>();
    private final Array<GiantCrop> giantCrops = new Array<>();
    private GameMap map;

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean plantFromSeed(String cropId, int tileX, int tileY) {
        CropData data = CropRegistry.getInstance().get(cropId);
        if (data == null) {
            System.err.println("Crop " + cropId + " not found");
            return false;
        }

        if (!map.canPlantInTile(tileX, tileY)) {
            return false;
        }

        crops.add(new com.proj.Model.inventoryItems.crops.Crop(data, tileX, tileY));
        map.getTile(tileX, tileY).setOccupied(true);
        map.getTile(tileX, tileY).setPassable(false);
        checkForGiantCrops();
        return true;
    }

    public void updateDaily(Season season) {
        for (int i = 0; i < crops.size; i++) {
            com.proj.Model.inventoryItems.crops.Crop crop = crops.get(i);
            checkForWater(crop);
            if (crop.getCropData().getSeason().contains(season, false)) {
                crop.grow();
            }
        }

        for (int i = 0; i < giantCrops.size; i++) {
            GiantCrop crop = giantCrops.get(i);
            checkForWater(crop);
            if (crop.getCropData().getSeason().contains(season, false)) {
                crop.grow();
            }
        }
    }

    private void checkForWater(com.proj.Model.inventoryItems.crops.Crop crop) {
        if (!crop.isWatered()) {
            crop.increaseDaysWithoutWater();
        }
        if (crop.getDaysWithoutWater() >= 2 && !crop.isFullyGrown()) {
            map.getTile(crop.getPosition().x, crop.getPosition().y).setOccupied(false);
            map.getTile(crop.getPosition().x, crop.getPosition().y).setPassable(true);
            removeCropAt(crop.getX(), crop.getY());
            return;
        }
        crop.setIsWatered(false);
    }

    private void growDaily(com.proj.Model.inventoryItems.crops.Crop crop, Season season) {
        if (crop.getCropData().getSeason().contains(season, false)) {
            crop.grow();
        }

        if (crop.isFullyGrown() &&
            crop.getCropData().getSeason().contains(season, false) &&
            !crop.hasProduct()) {
            crop.setHasProduct(true);
        }
    }


    private void checkForGiantCrops() {
        for (int x = 0; x < map.getMapWidth() - 1; x++) {
            for (int y = 0; y < map.getMapHeight() - 1; y++) {
                if (canFormGiantCrop(x, y)) {
                    formGiantCrop(x, y);
                }
            }
        }
    }

    private boolean canFormGiantCrop(int x, int y) {
        com.proj.Model.inventoryItems.crops.Crop crop1 = getCropAt(x, y);
        com.proj.Model.inventoryItems.crops.Crop crop2 = getCropAt(x + 1, y);
        com.proj.Model.inventoryItems.crops.Crop crop3 = getCropAt(x, y + 1);
        com.proj.Model.inventoryItems.crops.Crop crop4 = getCropAt(x + 1, y + 1);

        return crop1 != null && crop2 != null && crop3 != null && crop4 != null &&
            crop1.getCropData().equals(crop2.getCropData()) &&
            crop1.getCropData().equals(crop3.getCropData()) &&
            crop1.getCropData().equals(crop4.getCropData()) &&
//            crop1.isFullyGrown() && crop2.isFullyGrown() &&
//            crop3.isFullyGrown() && crop4.isFullyGrown() &&
            crop1.getCropData().canBecomeGiant();
    }

    private void formGiantCrop(int x, int y) {
        com.proj.Model.inventoryItems.crops.Crop baseCrop = getCropAt(x, y);
        int giantStage = Math.max(getCropAt(x, y).getStage(), getCropAt(x, y + 1).getStage());
        giantStage = Math.max(giantStage, getCropAt(x + 1, y).getStage());
        giantStage = Math.max(giantStage, getCropAt(x + 1, y + 1).getStage());

        boolean giantWater = getCropAt(x, y).isWatered() || getCropAt(x, y + 1).isWatered() ||
            getCropAt(x + 1, y + 1).isWatered() || getCropAt(x + 1, y).isWatered();
        boolean giantFertilized = getCropAt(x, y).isFertilized() || getCropAt(x, y + 1).isFertilized()
            || getCropAt(x + 1, y).isFertilized() || getCropAt(x + 1, y + 1).isFertilized();

//        boolean giantKood = false;
//        Crop.FertilizerType giant = null;
//        int deltaX[] = {0,1,0,1};
//        int deltaY[] = {0,1,1, 0};
//        for (int i = 0; i < deltaY.length; i++) {
//            if(getCropAt(x + deltaX[i], y + deltaY[i]) != null) {
//                if (getCropAt(x + deltaX[i], y + deltaY[i]).isFertilized()) {
//                    giantKood = true;
//                    giant = getCropAt(x + deltaX[i], y+deltaY[i]).getFertilizerType();
//                    break;
//                }
//            }
//        }
        removeCropAt(x, y);
        removeCropAt(x + 1, y);
        removeCropAt(x, y + 1);
        removeCropAt(x + 1, y + 1);

        GiantCrop giantCrop = new GiantCrop(baseCrop.getCropData(), x, y);
        giantCrop.setStage(giantStage);
        if (giantWater) {
            giantCrop.water();
        }
        if (giantFertilized) {
            giantCrop.fertilize();
        }
        giantCrops.add(giantCrop);

        map.getTile(x, y).setOccupied(true);
        map.getTile(x + 1, y).setOccupied(true);
        map.getTile(x, y + 1).setOccupied(true);
        map.getTile(x + 1, y + 1).setOccupied(true);
    }

    private void removeCropAt(int x, int y) {
        com.proj.Model.inventoryItems.crops.Crop crop = getCropAt(x, y);
        if (crop instanceof GiantCrop) {
            giantCrops.removeValue((GiantCrop) crop, true);
        } else if (crop != null) {
            crops.removeValue(crop, true);
        }
    }

    public GiantCrop getGiantCropAt(int x, int y) {
        for (GiantCrop giantCrop : giantCrops) {
            Point pos = giantCrop.getPosition();
            if (x >= pos.x && x <= pos.x + 1 && y >= pos.y && y <= pos.y + 1) {
                return giantCrop;
            }
        }
        return null;
    }

    private InventoryItem harvestGiantCrop(GiantCrop giantCrop) {
        InventoryItem item = giantCrop.harvest();
        int x = giantCrop.getX();
        int y = giantCrop.getY();
        map.getTile(x, y).setOccupied(false);
        map.getTile(x + 1, y).setOccupied(false);
        map.getTile(x, y + 1).setOccupied(false);
        map.getTile(x + 1, y + 1).setOccupied(false);

        map.getTile(x, y).setPassable(true);
        map.getTile(x + 1, y).setPassable(true);
        map.getTile(x, y + 1).setPassable(true);
        map.getTile(x + 1, y + 1).setPassable(true);

        giantCrops.removeValue(giantCrop, true);

        return item;
    }

    private void renderGiantCrop(SpriteBatch batch, GiantCrop giantCrop, int tileW, int tileH) {
        TextureRegion tex = giantCrop.getTexture();
        if (tex == null) return;
        if (giantCrop.isFullyGrown()) {
            float drawX = giantCrop.getX() * tileW;
            float drawY = giantCrop.getY() * tileH;
            float width = 2 * tileW;
            float height = 2 * tileH;
            batch.draw(tex, drawX, drawY, width, height);
        } else {
            float drawX = giantCrop.getX() * tileW;
            float drawY = giantCrop.getY() * tileH;
            float width = 2 * tileW;
            float height = 2 * tileH;
            batch.draw(tex, drawX, drawY, width, height);
        }
    }


    public InventoryItem harvestAt(int tileX, int tileY) {
        GiantCrop giantCrop = getGiantCropAt(tileX, tileY);
        if (giantCrop != null) {
            return harvestGiantCrop(giantCrop);
        }
        com.proj.Model.inventoryItems.crops.Crop crop = getCropAt(tileX, tileY);
        if (crop == null || !crop.hasProduct()) return null;
        InventoryItem item = crop.harvest();
        if (crop.getCropData().getRegrowthTime() <= 0) {
            map.getTile(tileX, tileY).setOccupied(false);
            map.getTile(tileX, tileY).setPassable(true);
            crops.removeValue(crop, true);
        }

        return item;
    }

    public InventoryItem chopAt(int tileX, int tileY) {
        GiantCrop giantCrop = getGiantCropAt(tileX, tileY);
        if (giantCrop != null) {
            return chopGiantCrop(giantCrop);
        }
        com.proj.Model.inventoryItems.crops.Crop crop = getCropAt(tileX, tileY);
        if (crop == null) return null;
        return chopCrop(crop, tileX, tileY);
    }

    private InventoryItem chopCrop(com.proj.Model.inventoryItems.crops.Crop crop, int tileX, int tileY) {
        InventoryItem item = crop.chop();
        map.getTile(tileX, tileY).setOccupied(false);
        map.getTile(tileX, tileY).setPassable(true);
        crops.removeValue(crop, true);
        return item;
    }

    private InventoryItem chopGiantCrop(GiantCrop giantCrop) {
        InventoryItem item = null;
        if (giantCrop.isFullyGrown()) {
            item = giantCrop.harvest();
        }
        int x = giantCrop.getX();
        int y = giantCrop.getY();
        map.getTile(x, y).setOccupied(false);
        map.getTile(x + 1, y).setOccupied(false);
        map.getTile(x, y + 1).setOccupied(false);
        map.getTile(x + 1, y + 1).setOccupied(false);

        map.getTile(x, y).setPassable(true);
        map.getTile(x + 1, y).setPassable(true);
        map.getTile(x, y + 1).setPassable(true);
        map.getTile(x + 1, y + 1).setPassable(true);
        giantCrops.removeValue(giantCrop, true);
        return item;
    }

    public void renderAll(SpriteBatch batch, Season season) {
        int tileW = map.getTileWidth();
        int tileH = map.getTileHeight();

        for (com.proj.Model.inventoryItems.crops.Crop crop : crops) {
            TextureRegion tex = crop.getTexture();

            if (tex == null) {
                continue;
            }

            float texW = tex.getRegionWidth() / 2f;
            float texH = tex.getRegionHeight() / 2f;

            float drawX = crop.getX() * tileW + (tileW - texW) / 2f;
            float drawY = crop.getY() * tileH;
            batch.draw(
                tex,
                drawX, drawY,
                texW, texH
            );
        }

        for (GiantCrop giantCrop : giantCrops) {
            renderGiantCrop(batch, giantCrop, tileW, tileH);
        }
    }


    public com.proj.Model.inventoryItems.crops.Crop getCropAt(int x, int y) {
        for (com.proj.Model.inventoryItems.crops.Crop crop : crops) {
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

