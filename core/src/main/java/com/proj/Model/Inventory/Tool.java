package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Tool extends InventoryItem {

    private ToolType type;
    private int level;
    private float energyCost;
    private boolean inUse;
    private float useAnimationTimer;

    public Tool(String id, String name, TextureRegion texture, ToolType type, int level) {
        super(id, name, texture, false, 1);
        this.type = type;
        this.level = level;
        this.energyCost = calculateEnergyCost();
        this.inUse = false;
        this.useAnimationTimer = 0;
    }

    private float calculateEnergyCost() {
        float baseCost = getBaseEnergyCost();
        return Math.max(1.0f, baseCost - (level - 1));
    }

    protected abstract float getBaseEnergyCost();

    public abstract boolean useOnTile(int tileX, int tileY);

    @Override
    public void use() {
        if (!inUse) {
            inUse = true;
            useAnimationTimer = 0.5f;
        }
    }

    public void update(float delta) {
        if (inUse) {
            useAnimationTimer -= delta;
            if (useAnimationTimer <= 0) {
                inUse = false;
            }
        }
    }

    public void upgrade() {
        if (level < 5) {
            level++;
            energyCost = calculateEnergyCost();
        }
    }

    public float getEnergyCost() {
        return energyCost;
    }

    public ToolType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public boolean isInUse() {
        return inUse;
    }

    public float getUseAnimationProgress() {
        if (!inUse) return 0;
        return 1 - (useAnimationTimer / 0.5f);
    }

    @Override
    public String getName() {
        String levelName;
        switch (level) {
            case 1: levelName = "Basic"; break;
            case 2: levelName = "Copper"; break;
            case 3: levelName = "Steel"; break;
            case 4: levelName = "Gold"; break;
            case 5: levelName = "Iridium"; break;
            default: levelName = "Basic";
        }
        return levelName + " " + super.getName();
    }
}

