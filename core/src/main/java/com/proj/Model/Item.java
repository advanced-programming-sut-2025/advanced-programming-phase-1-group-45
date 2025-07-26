package com.proj.Model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {
    protected String name;
    protected String description;
    protected int baseSellPrice;
    protected TextureRegion textureRegion;
    protected boolean isStackable;
    protected ItemType type;

    public Item(String name, String description, int baseSellPrice, TextureRegion textureRegion, boolean isStackable, ItemType type) {
        this.name = name;
        this.description = description;
        this.baseSellPrice = baseSellPrice;
        this.textureRegion = textureRegion;
        this.isStackable = isStackable;
        this.type = type;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBaseSellPrice() { return baseSellPrice; }
    public TextureRegion getTextureRegion() { return textureRegion; }
    public boolean isStackable() { return isStackable; }
    public ItemType getType() { return type; }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }
}
