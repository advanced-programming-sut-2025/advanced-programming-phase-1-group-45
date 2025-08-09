package com.proj.Model.inventoryItems.trees;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.InventoryItemFactory;
import com.proj.Model.inventoryItems.seeds.ItemRegistry;
import com.proj.map.Season;

import java.awt.*;

public class Tree {
    private final TreeData data;
    private final int x, y;
    private int currentStage = 4;
    private int daysInCurrentStage = 0;
    private boolean hasProduct = false;

    public Tree(TreeData data, int x, int y) {
        this.data = data;
        this.x = x;
        this.y = y;
    }

    public void grow() {
        if (isFullyGrown()) return;

        daysInCurrentStage++;

        if (daysInCurrentStage >= data.getDaysPerStage()[currentStage]) {
            currentStage++;
            daysInCurrentStage = 0;
        }
    }

    public boolean isFullyGrown() {
        return currentStage >= data.getGrowthStage() - 1;
    }

    public boolean isFruitTree() {
        return data.isFruitTree();
    }

    public InventoryItem harvest() {
        if (!hasProduct || !data.isFruitTree()) return null;

        int[] countRange = data.getProductCount();
        int count = MathUtils.random(countRange[0], countRange[1]);
        hasProduct = false;
        return ItemRegistry.getInstance().get(data.getProduct());
    }

    public Array<InventoryItem> chop() {
        Array<InventoryItem> items = new Array<>();

        if (data.getChopProduct() != null) {
            int[] chopRange = data.getChopProductCount();
            int chopCount = MathUtils.random(chopRange[0], chopRange[1]);
            for (String item : data.getChopProduct()) {
                items.add(ItemRegistry.getInstance().get(item));
            }
        }
        items.add(InventoryItemFactory.createItem("Wood", 1));
        return items;
    }

    public TextureRegion getTexture(Season season) {
        if (currentStage < data.getGrowthStage() - 1) {
            return data.getTextureForStage(currentStage);
        } else {
            if (hasProduct) {
                return data.getFruitTexture();
            } else {
                return data.getSeasonalTexture(season);
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

    public TreeData getTreeData() {
        return data;
    }
}
