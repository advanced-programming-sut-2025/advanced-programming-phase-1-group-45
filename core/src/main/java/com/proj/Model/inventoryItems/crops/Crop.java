package com.proj.Model.inventoryItems.crops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;
import com.proj.map.Season;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.InventoryItemFactory;

import java.awt.*;

public class Crop {
    private final CropData data;
    private final int x, y;
    private int currentStage = 4;
    private int daysInCurrentStage = 0;
    private boolean hasProduct = false;
    private boolean isWatered;
    private boolean isFertilized;
    private int daysWithoutWater = 0;
    private int daysUntilNextHarvest = 0;
    private boolean isFullyGrown = false;
    private boolean showWaterEffect = false;
    private float waterEffectTimer = 0f;
    private boolean showFertilizeEffect = false;
    private float fertilizeEffectTimer = 0f;

    public Crop(CropData data, int x, int y) {
        this.data = data;
        this.x = x;
        this.y = y;
        if (isFullyGrown()) {
            hasProduct = true;
        }
    }

    public void grow() {
        if (!isFullyGrown) {
            daysInCurrentStage++;
            if (daysInCurrentStage >= data.getGrowthStages()[currentStage]) {
                currentStage++;
                daysInCurrentStage = 0;
                if (currentStage >= data.getGrowthStages().length - 1) {
                    isFullyGrown = true;
                    hasProduct = true;
                }
            }
        }
        else if (!hasProduct) {
            if (daysUntilNextHarvest > 0) {
                daysUntilNextHarvest--;

                if (daysUntilNextHarvest <= 0) {
                    hasProduct = true;
                }
            }
        }
    }

    public void water() {
        isWatered = true;
        daysWithoutWater = 0;
        showWaterEffect = true;
        waterEffectTimer = 1.0f; // نمایش افکت به مدت 1 ثانیه
    }


    public void fertilize() {
        isFertilized = true;
    }

    public void updateEffects(float delta) {
        // به‌روزرسانی تایمرهای افکت
        if (waterEffectTimer > 0) {
            waterEffectTimer -= delta;
            if (waterEffectTimer <= 0) {
                showWaterEffect = false;
            }
        }

        if (fertilizeEffectTimer > 0) {
            fertilizeEffectTimer -= delta;
            if (fertilizeEffectTimer <= 0) {
                showFertilizeEffect = false;
            }
        }
    }
    public boolean isShowingWaterEffect() {
        return showWaterEffect;
    }

    public boolean isShowingFertilizeEffect() {
        return showFertilizeEffect;
    }

    public boolean isFullyGrown() {
        return currentStage >= data.getGrowthStages().length - 1;
    }

    public InventoryItem harvest() {
        if (!hasProduct) return null;
        Integer[] countRange = data.getProductCount();
        int count = MathUtils.random(countRange[0], countRange[1]);
        InventoryItem item = ItemRegistry.getInstance().get(data.getName());
        item.setQuantity(count);
        hasProduct = false;
        if (data.getRegrowthTime() > 0) {
            daysUntilNextHarvest = data.getRegrowthTime();
        }
        return item;
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

    public int getStage() {
        return currentStage;
    }

    public void setStage(int stage) {
        currentStage = stage;
    }

    public boolean isWatered() {
        return isWatered;
    }

    public boolean isFertilized() {
        return isFertilized;
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

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public void increaseDaysWithoutWater() {
        this.daysWithoutWater++;
    }

    public CropData getData() {
        return data;
    }

    public int getDaysWithoutWater() {
        return daysWithoutWater;
    }

    public boolean isHasProduct() {
        return hasProduct;
    }

    public void setIsWatered(boolean isWatered) {
        this.isWatered = isWatered;
    }
    public void setDaysUntilNextHarvest(int days) {
        this.daysUntilNextHarvest = days;
    }
}
