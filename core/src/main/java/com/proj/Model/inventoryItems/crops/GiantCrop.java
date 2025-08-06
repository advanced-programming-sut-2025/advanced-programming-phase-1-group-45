package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;

import java.awt.*;

public class GiantCrop extends Crop {
    public GiantCrop(CropData data, int x, int y) {
        super(data, x, y);
    }

    @Override
    public TextureRegion getTexture() {
        if (!isFullyGrown()) {
            return getCropData().getTextureForStage(getStage());
        }
        if (hasProduct()) {
            return getCropData().getGiantTexture();
        }
        return getCropData().getTextureForStage(getStage());
    }

    @Override
    public InventoryItem harvest() {
        if (!hasProduct()) return null;
        if(getCropData().isOneTimeHarvest()) {
            return chop();
        }
        setHasProduct(false);
        InventoryItem item =  ItemRegistry.getInstance().get(getCropData().getName());
        item.setQuantity(10); //a giant crop == 10 normal crop
        return item;
    }

    @Override
    public Point getPosition() {
        return new Point(getX(), getY());
    }
}
