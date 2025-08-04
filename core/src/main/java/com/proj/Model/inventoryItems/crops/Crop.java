package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;

import java.awt.*;

public class Crop {
    private final CropData data;
    private final int x, y;
    private int currentStage = 3;
    private int daysInCurrentStage = 0;
    private boolean hasProduct = false;

    public Crop(CropData data, int x, int y) {
        this.data = data;
        this.x = x;
        this.y = y;
        if (isFullyGrown()) {
            hasProduct = true;
        }
    }

    public void grow() {
        if (isFullyGrown()) return;

        daysInCurrentStage++;

        if (daysInCurrentStage >= data.getGrowthStages()[currentStage]) {
            currentStage++;
            daysInCurrentStage = 0;
        }
    }

    public boolean isFullyGrown() {
        return currentStage >= data.getGrowthStages().length - 1;
    }

    public InventoryItem harvest() {
        if (!hasProduct) return null;

        Integer[] countRange = data.getProductCount();
        hasProduct = false;
        return ItemRegistry.getInstance().get(data.getName());
    }

    public InventoryItem chop() {
        InventoryItem item = null;
        if (hasProduct) item = (harvest());
        return item;
    }

    public TextureRegion getTexture() {
        if (currentStage < data.getGrowthStages().length - 2) {
            return data.getTextureForStage(currentStage);
        } else {
            if (hasProduct) {
                return data.getHasProductTexture();
            } else {
                return data.getTextureForStage(currentStage);
            }
        }
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasProduct() {
        return hasProduct;
    }

    public void setHasProduct(boolean hasProduct) {
        this.hasProduct = hasProduct;
    }

    public CropData getCropData() {
        return data;
    }
}
