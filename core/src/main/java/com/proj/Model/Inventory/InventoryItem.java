package com.proj.Model.Inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class InventoryItem {
    private String id;
    private String name;
    private TextureRegion texture;
    private int quantity;
    private boolean stackable;
    private int maxStackSize;

    public InventoryItem(String id, String name, TextureRegion texture, boolean stackable, int maxStackSize) {
        this.id = id;
        this.name = name;
        this.texture = texture;
        this.quantity = 1;
        this.stackable = stackable;
        this.maxStackSize = maxStackSize > 0 ? maxStackSize : 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public void increaseQuantity(int amount) {
        if (stackable) {
            this.quantity = Math.min(this.quantity + amount, maxStackSize);
        }
    }

    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    public boolean canStack(InventoryItem other) {
        return stackable && other.stackable && this.id.equals(other.id) && this.quantity < maxStackSize;
    }

    public boolean isStackable() {
        return stackable;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public abstract void use();
}
